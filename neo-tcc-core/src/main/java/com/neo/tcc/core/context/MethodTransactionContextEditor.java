package com.neo.tcc.core.context;

import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.api.TransactionContextEditor;
import com.neo.tcc.core.utils.CompensableMethodUtils;

import java.lang.reflect.Method;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/7 09:36
 * @Description:
 */
public class MethodTransactionContextEditor implements TransactionContextEditor {
    @Override
    public TransactionContext get(Object target, Method method, Object[] args) {
        int position = CompensableMethodUtils.getTransactionContextParamPosition(method.getParameterTypes());
        if (position >= 0) {
            return (TransactionContext) args[position];
        }
        return null;
    }

    @Override
    public void set(TransactionContext context, Object target, Method method, Object[] args) {
        int position = CompensableMethodUtils.getTransactionContextParamPosition(method.getParameterTypes());
        if (position >= 0) {
            args[position] = context;
        }
    }
}
