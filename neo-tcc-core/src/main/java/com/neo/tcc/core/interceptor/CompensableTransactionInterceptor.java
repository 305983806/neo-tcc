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
                return rootMethodProceed(pjp, asyncConfirm, asyncCancel);
            case PROVIDER:
                return providerMethodProceed(pjp, transactionContext, asyncConfirm, asyncCancel);
            default:
                return pjp.proceed();
        }
    }

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
            transactionManager.cleanAfterCompletion(transaction);
        }
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
