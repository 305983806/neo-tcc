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
     * 计算方法类型( MethodType )的目的，可以根据不同方法类型，做不同的事务处理。
     * 当满足以下2个条件时，返回 ROOT 方法类型（表示发起根事务）：
     * - 事务传播级别为 Propagation.REQUIRED，并且当前没有事；
     * - 事务传播级别为 Propagation.REQUIRES_NEW，新建事务，如果当前存在事务，把当前事务挂起。此时，事务管理器的当前线程事务队列可能会存在多个事务。
     * 当满足以下3个条件时，返回 PROVIDER 方法类型（表示发起分支事务）：
     * - 事务传播级别为 Propagation.REQUIRED，并且当前不存在事务，并且方法参数传递了事务上下文。
     * - 事务传播级别为 Propagation.MANDATORY，并且当前不存在事务，并且方法参数传递了事务上下文。
     * 方法类型为 MethodType.Normal 时，不进行事务处理。
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
