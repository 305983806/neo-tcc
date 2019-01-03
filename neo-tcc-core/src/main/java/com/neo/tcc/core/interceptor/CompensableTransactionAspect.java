package com.neo.tcc.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/3 16:48
 * @Description: 可补偿事务切面
 */
@Aspect
public abstract class CompensableTransactionAspect {
    private CompensableTransactionInterceptor compensableTransactionInterceptor;

    public void setCompensableTransactionInterceptor(CompensableTransactionInterceptor compensableTransactionInterceptor) {
        this.compensableTransactionInterceptor = compensableTransactionInterceptor;
    }

    @Pointcut("@annotation(com.neo.tcc.core.api.Compensable)")
    public void compensableService() {

    }

    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) {
        return compensableTransactionInterceptor.interceptCompensableMethod(pjp);
    }
}
