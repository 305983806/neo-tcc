package com.neo.tcc.core.interceptor;

import com.alibaba.fastjson.JSON;
import com.neo.tcc.core.NoExistedTransactionException;
import com.neo.tcc.core.SystemException;
import com.neo.tcc.core.Transaction;
import com.neo.tcc.core.TransactionManager;
import com.neo.tcc.core.api.Compensable;
import com.neo.tcc.core.api.Propagation;
import com.neo.tcc.core.api.TransactionContext;
import com.neo.tcc.core.api.TransactionStatus;
import com.neo.tcc.core.common.MethodType;
import com.neo.tcc.core.support.FactoryBuilder;
import com.neo.tcc.core.utils.CompensableMethodUtils;
import com.neo.tcc.core.utils.ReflectionUtils;
import com.neo.tcc.core.utils.TransactionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/3 16:51
 * @Description: 可补偿事务拦截器
 */
public class CompensableTransactionInterceptor {
    static final Logger log = LoggerFactory.getLogger(CompensableTransactionInterceptor.class);

    private TransactionManager transactionManager;
    private Set<Class<? extends Exception>> delayCancelExceptions;

    public CompensableTransactionInterceptor(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * 当调用
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {
        // 获得带 @Compensable 注解的方法
        Method method = CompensableMethodUtils.getCompensableMethod(pjp);

        Compensable compensable = method.getAnnotation(Compensable.class);
        Propagation propagation = compensable.propagation();
        // 获得事务上下文
        TransactionContext transactionContext = FactoryBuilder
                .factoryOf(compensable.transactionContextEditor())
                .getInstance()
                .get(pjp.getTarget(), method, pjp.getArgs());

        // 是否异步确认
        boolean asyncConfirm = compensable.asyncConfirm();
        // 是否异常取消
        boolean asyncCancel = compensable.asyncCancel();
        // 当前线程是否在事务中
        boolean isTransactionActive = transactionManager.isTransactionActive();
        // 判断事务上下文是否合法
        if (!TransactionUtils.isLegalTransactionContext(isTransactionActive, propagation, transactionContext)) {
            throw new SystemException("no active compensable transaction while propagation is mandatory for method " + method.getName());
        }
        // 计算方法类型
        MethodType methodType = CompensableMethodUtils.calculateMethodType(propagation, isTransactionActive, transactionContext);
        // 处理
        switch (methodType) {
            case ROOT:
                // 当方法类型为 MethodType.ROOT 时，调用 {@link #rootMethodProceed(ProceedingJoinPoint, boolean, boolean)} 方法，发起 TCC 整体流程
                return rootMethodProceed(pjp, asyncConfirm, asyncCancel);
            case PROVIDER:
                // 当方法类型为 Propagation.PROVIDER 时，服务提供者参与 TCC 整体流程。
                return providerMethodProceed(pjp, transactionContext, asyncConfirm, asyncCancel);
            default:
                return pjp.proceed();
        }
    }

    /**
     * 发起 TCC 整体流程<br/>
     * 调用 {@link TransactionManager#begin()} 方法，发起根事务， TCC try 阶段开始<br/>
     * 调用 {@link ProceedingJoinPoint#proceed()} 方法，执行方法原逻辑（即 try 逻辑）<br/>
     * 当原逻辑执行异常时，TCC try 阶段失败，调用 {@link TransactionManager#rollback(boolean)} 方法，TCC cancel 阶段，回滚事务。<br/>
     * 此处 {@link #isDelayCancelException(Throwable)} 方法，判断异常是否为延迟取消回滚异常，部分异常不适合立即回滚事务。<br/>
     * 当原逻辑执行成功时，TCC try 阶段成功，调用 {@link TransactionManager#commit(boolean)} 方法，TCC confirm 阶段，提交事务。<br/>
     * 调用 {@link TransactionManager#cleanAfterCompletion(Transaction)} 方法，将事务从当前线程事务队列移除，避免线程冲突。<br/>
     *
     * @param pjp 切面对象
     * @param asyncConfirm 是否异步提交
     * @param asyncCancel 是否异步取消
     * @return 执行结果对象
     * @throws Throwable
     */
    private Object rootMethodProceed(ProceedingJoinPoint pjp, boolean asyncConfirm, boolean asyncCancel) throws Throwable {
        Object returnValue;
        Transaction transaction = null;
        try {
            // 发起根事务
            transaction = transactionManager.begin();
            // 执行方法原逻辑
            returnValue = pjp.proceed();
            // 提交事务
            transactionManager.commit(asyncConfirm);
        } catch (Throwable tryingException) {
            if (!isDelayCancelException(tryingException)) {
                log.warn(String.format("compensable transaction trying failed. transaction content:%s",
                        JSON.toJSONString(transaction)),
                        tryingException);
                transactionManager.rollback(asyncCancel);
            }
            throw tryingException;
        } finally {
            // 清理事务
            transactionManager.cleanAfterCompletion(transaction);
        }
        return returnValue;
    }

    /**
     * 当事务处于 {@link TransactionStatus#TRYING} 时，调用 {@link TransactionManager#propagationExistBegin(TransactionContext)} 方法，
     * 传播发起分支事务。发起分支事务完成后，调用 {@link ProceedingJoinPoint#proceed()} 方法，执行方法原逻辑（即 Try 逻辑）。<br/>
     * 当事务处理 {@link TransactionStatus#CONFIRMING} 时，调用 {@link TransactionManager#commit(boolean)} 方法提交事务。<br/>
     * 当事务处于 {@link TransactionStatus#CANCELLING} 时，调用 {@link TransactionManager#rollback(boolean)} 方法提交事务。<br/>
     * 调用 {@link TransactionManager#cleanAfterCompletion(Transaction)} 方法，将事务从当前线程队列移除，避免线程冲突。<br/>
     * 当事务处于 {@link TransactionStatus#CONFIRMING} 或 {@link TransactionStatus#CANCELLING} 时，最终会调用 {@link ReflectionUtils#getNullValue(Class)} 方法，
     * 返回空值。为什么返回空值？Confirm/Cancel 相关方法，是通过 AOP 切面调用，只调用，不返回值，但方法不能没有返回值，因此直接返回空值。<br/>
     * 当方法类型为 Propagation.NORMAL 时，执行方法原逻辑，不进行事务处理。
     *
     * @param pjp
     * @param transactionContext
     * @param asyncConfirm
     * @param asyncCancel
     * @return
     * @throws Throwable
     */
    private Object providerMethodProceed(ProceedingJoinPoint pjp, TransactionContext transactionContext, boolean asyncConfirm, boolean asyncCancel) throws Throwable {
        Transaction transaction = null;
        try {
            switch (TransactionStatus.valueOf(transactionContext.getStatus())) {
                case TRYING:
                    transaction = transactionManager.propagationNewBegin(transactionContext);
                    return pjp.proceed();
                case CONFIRMING:
                    try {
                        transaction = transactionManager.propagationExistBegin(transactionContext);
                        transactionManager.commit(asyncConfirm);
                        break;
                    } catch (NoExistedTransactionException e) {
                        // 事务已提交，可以忽略异常处理。
                    }
                case CANCELLING:
                    try {
                        transaction = transactionManager.propagationExistBegin(transactionContext);
                        transactionManager.rollback(asyncCancel);
                    } catch (NoExistedTransactionException e) {
                        // 事务已取消，可以忽略异常处理。
                    }
            }
        } finally {
            // 将事务从当前线程事务队列移除
            transactionManager.cleanAfterCompletion(transaction);
        }
        // 返回空值
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        return ReflectionUtils.getNullValue(method.getReturnType());
    }

    private boolean isDelayCancelException(Throwable throwable) {
        if (delayCancelExceptions != null) {
            for (Class delayCancelException : delayCancelExceptions) {
                Throwable rootCause = ExceptionUtils.getRootCause(throwable);
                if (delayCancelException.isAssignableFrom(throwable.getClass()) || (rootCause != null && delayCancelException.isAssignableFrom(rootCause.getClass()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
