package com.neo.tcc.core.api;

import java.lang.reflect.Method;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 22:03
 * @Description: 事务上下文编辑器接口
 */
public interface TransactionContextEditor {
    /**
     * 从参数中获得事务上下文
     *
     * @param target 对象
     * @param method 方法
     * @param args 参数
     * @return 事务上下文
     */
    TransactionContext get(Object target, Method method, Object[] args);

    /**
     * 设置事务上下文到参数中
     *
     * @param context 事务上下文
     * @param target 对象
     * @param method 方法
     * @param args 参数
     */
    void set(TransactionContext context, Object target, Method method, Object[] args);
}
