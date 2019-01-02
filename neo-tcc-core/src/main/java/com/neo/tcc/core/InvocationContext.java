package com.neo.tcc.core;

import java.io.Serializable;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:40
 * @Description: 执行方法调用的上下文
 */
public class InvocationContext implements Serializable {
    /**
     * 类
     */
    private Class targetClass;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数类型数组
     */
    private Class[] parameterTypes;
    /**
     * 参数数组
     */
    private Object[] args;

    public InvocationContext(Class targetClass, String methodName, Class[] parameterTypes, Object... args) {
        this.targetClass = targetClass;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getArgs() {
        return args;
    }
}
