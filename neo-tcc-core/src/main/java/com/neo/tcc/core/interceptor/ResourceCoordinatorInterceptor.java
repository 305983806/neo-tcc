package com.neo.tcc.core.interceptor;

import com.neo.tcc.core.InvocationContext;
import com.neo.tcc.core.Participant;
import com.neo.tcc.core.Transaction;
import com.neo.tcc.core.TransactionManager;
import com.neo.tcc.core.api.Compensable;
import com.neo.tcc.core.api.TransactionContext;
import com.neo.tcc.core.api.TransactionId;
import com.neo.tcc.core.api.TransactionStatus;
import com.neo.tcc.core.support.FactoryBuilder;
import com.neo.tcc.core.utils.CompensableMethodUtils;
import com.neo.tcc.core.utils.ReflectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/4 17:35
 * @Description: 资源协调者拦截器
 */
public class ResourceCoordinatorInterceptor {
    private TransactionManager transactionManager;

    public ResourceCoordinatorInterceptor(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * 当事务处理 {@link TransactionStatus#TRYING} 时，调用 {@link #addParticipant(ProceedingJoinPoint)} 方法，添加事务参与者。<br/>
     * 调用 {@link ProceedingJoinPoint#proceed()} 方法，执行方法原逻辑。
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {
        Transaction transaction = transactionManager.getCurrentTransaction();
        if (transaction != null) {
            switch (transaction.getStatus()) {
                case TRYING:
                    // 添加事务参与者
                    addParticipant(pjp);
                    break;
                case CONFIRMING:
                    break;
                case CANCELLING:
                    break;
            }
        }
        // 执行方法原逻辑
        return pjp.proceed(pjp.getArgs());
    }

    /**
     * 调用 {@link CompensableMethodUtils#getCompensableMethod(ProceedingJoinPoint)} 方法，获得带 @Compensable 注解的方法。<br/>
     * 调用 {@link TransactionManager#getCurrentTransaction()} 方法，获取事务。<br/>
     * 调用 {@link TransactionId#TransactionId(byte[])} 构造方法，创建分支事务编号。<br/>
     * 调用 {@link ReflectionUtils#getDeclaringType(Class, String, Class[])} 方法，获得声明 @Compensable 方法和实际类。<br/>
     * 调用 {@link InvocationContext} 的构造方法，分别创建确认执行方法调用上下文和取消执行方法调用上下文。<br/>
     * 调用 {@link Participant#Participant(TransactionId, InvocationContext, InvocationContext, Class)} 构造方法，创建事务参与者。
     *
     * @param pjp
     */
    private void addParticipant(ProceedingJoinPoint pjp) {
        // 获得 @Compensable 注解
        Method method = CompensableMethodUtils.getCompensableMethod(pjp);
        if (method == null) {
            throw new RuntimeException(String.format("join point not found method, point is : %s", pjp.getSignature().getName()));
        }
        Compensable compensable = method.getAnnotation(Compensable.class);
        // 获得 确认执行业务方法 和 取消执行业务方法 名称
        String confirmMethodName = compensable.confirmMethod();
        String cancelMethodName = compensable.cancelMethod();
        // 获得当前线程事务第一个（头部）元素
        Transaction transaction = transactionManager.getCurrentTransaction();
        // 创建事务编号
        TransactionId id = new TransactionId(transaction.getId().getGlobalTransactionId());

        if (FactoryBuilder.factoryOf(compensable.transactionContextEditor()).getInstance().get(pjp.getTarget(), method, pjp.getArgs()) == null) {
            FactoryBuilder.factoryOf(
                    compensable.transactionContextEditor()).getInstance().set(new TransactionContext(id, TransactionStatus.TRYING.getId()),
                    pjp.getTarget(),
                    ((MethodSignature)pjp.getSignature()).getMethod(),
                    pjp.getArgs());
        }
        // 获得类
        Class targetClass = ReflectionUtils.getDeclaringType(
                pjp.getTarget().getClass(),
                method.getName(),
                method.getParameterTypes());
        // 创建 确认执行方法调用上下文 和取消执行方法调用上下文
        InvocationContext confirmInvocation = new InvocationContext(
                targetClass,
                confirmMethodName,
                method.getParameterTypes(),
                pjp.getArgs());
        InvocationContext cancelInvocation = new InvocationContext(
                targetClass,
                cancelMethodName,
                method.getParameterTypes(),
                pjp.getArgs());
        // 创建事务参与者
        Participant participant = new Participant(
                id,
                confirmInvocation,
                cancelInvocation,
                compensable.transactionContextEditor());
        // 添加事务参与者到事务
        transactionManager.addParticipant(participant);
    }
}
