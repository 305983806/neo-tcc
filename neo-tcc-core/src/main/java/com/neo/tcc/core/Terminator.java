package com.neo.tcc.core;

import com.neo.tcc.core.api.TransactionContext;
import com.neo.tcc.core.api.TransactionContextEditor;
import com.neo.tcc.core.support.FactoryBuilder;
import com.neo.tcc.core.utils.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:41
 * @Description:
 */
public class Terminator implements Serializable {

    public Object invoke(TransactionContext transactionContext, InvocationContext invocationContext, Class<? extends TransactionContextEditor> transactionContextEditorClass) {
        if (StringUtils.isNotEmpty(invocationContext.getMethodName())) {
            try {
                // 获得参与者对象
                Object target = FactoryBuilder.factoryOf(invocationContext.getTargetClass()).getInstance();
                // 获得方法
                Method method = target.getClass().getMethod(invocationContext.getMethodName(), invocationContext.getParameterTypes());
                // 设置事务上下文到方法参数
                FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().set(transactionContext, target, method, invocationContext.getArgs());
                // 执行方法
                return method.invoke(target, invocationContext.getArgs());
            } catch (Exception e) {
                throw new SystemException(e);
            }
        }
        return null;
    }
}
