package com.neo.tcc.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/4 17:34
 * @Description:
 */
@Aspect
public abstract class ResourceCoordinatorAspect {
    private ResourceCoordinatorInterceptor resourceCoordinatorInterceptor;

    @Pointcut("@annotation(com.neo.tcc.core.api.Compensable)")
    public void transactionContextCall() {}

    @Around("transactionContextCall()")
    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {
        return resourceCoordinatorInterceptor.interceptTransactionContextMethod(pjp);
    }

    public abstract int getOrder();
}
