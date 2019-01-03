package com.neo.tcc.core.utils;

import com.neo.tcc.core.api.Compensable;
import com.neo.tcc.core.api.Propagation;
import com.neo.tcc.core.api.TransactionContext;
import com.neo.tcc.core.common.MethodType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/3 16:56
 * @Description:
 */
public class CompensableMethodUtils {
    /**
     * 获得带 @Compensable 注解的方法
     *
     * @param pjp 切面点
     * @return 方法
     */
    public static Method getCompensableMethod(ProceedingJoinPoint pjp) {
        // 代理方法对象
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        if (method.getAnnotation(Compensable.class) == null) {
            try {
                // 实际方法对象
                method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        return method;
    }

    /**
     * 计算方法类型
     *
     * @param propagation 传播级别
     * @param isTransactionActive 是否开启事务
     * @param context 事务上下文
     * @return 方法类型
     */
    public static MethodType calculateMethodType(Propagation propagation, boolean isTransactionActive, TransactionContext context) {
        if ((propagation.equals(Propagation.REQUIRED) && !isTransactionActive && context == null) || propagation.equals(Propagation.REQUIRES_NEW)) {
            return MethodType.ROOT;
        } else if (propagation.equals(Propagation.REQUIRED) || propagation.equals(Propagation.MANDATORY)) {
            return MethodType.PROVIDER;
        } else {
            return MethodType.NORMAL;
        }
    }

    public static MethodType calculateMethodType(TransactionContext context, boolean isCompensable) {
        if (context == null && isCompensable) {
            return MethodType.ROOT;
        } else if (context == null && !isCompensable) {
            return MethodType.CONSUMER;
        } else if (context != null && isCompensable) {
            return MethodType.PROVIDER;
        } else {
            return MethodType.NORMAL;
        }
    }
}
