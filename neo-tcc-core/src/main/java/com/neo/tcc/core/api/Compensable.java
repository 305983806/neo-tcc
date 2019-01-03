package com.neo.tcc.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/3 11:50
 * @Description: 标记可补偿的方法注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Compensable {
    /**
     * 传播级别
     *
     * @return
     */
    Propagation propagation() default Propagation.REQUIRED;

    /**
     * 确认执行业务方法
     *
     * @return
     */
    String confirmMethod() default "";

    /**
     * 取消执行业务方法
     *
     * @return
     */
    String cancelMethod() default "";

    /**
     * 事务上下文编辑类
     *
     * @return
     */
    Class<? extends TransactionContextEditor> transactionContextEditor() default DefaultTransactionContextEditor.class;

    boolean asyncConfirm() default false;

    boolean asyncCancel() default false;

    /**
     * 无事务上下文编辑器实现
     */
    class NullableTransactionContextEditor implements TransactionContextEditor {

        @Override
        public TransactionContext get(Object target, Method method, Object[] args) {
            return null;
        }

        @Override
        public void set(TransactionContext context, Object target, Method method, Object[] args) {
        }
    }

    /**
     * 默认事务上下文编辑器实现
     */
    class DefaultTransactionContextEditor implements TransactionContextEditor {

        @Override
        public TransactionContext get(Object target, Method method, Object[] args) {
            int position = getTransactionContextParamPosition(method.getParameterTypes());
            if (position >= 0) {
                return (TransactionContext) args[position];
            }
            return null;
        }

        @Override
        public void set(TransactionContext context, Object target, Method method, Object[] args) {
            int position = getTransactionContextParamPosition(method.getParameterTypes());
            if (position >= 0) {
                args[position] = context;
            }
        }

        /**
         * 获得事务上下文在方法参数里的位置
         * @param parameterTypes
         * @return
         */
        public static int getTransactionContextParamPosition(Class<?>[] parameterTypes) {
            int position = -1;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i].equals(TransactionContext.class)) {
                    position = i;
                    break;
                }
            }
            return position;
        }

        public static TransactionContext getTransactionContextFromArgs(Object[] args) {
            TransactionContext transactionContext = null;
            for (Object arg : args) {
                if (arg != null && TransactionContext.class.isAssignableFrom(arg.getClass())) {
                    transactionContext = (TransactionContext) arg;
                }
            }
            return transactionContext;
        }
    }
}
