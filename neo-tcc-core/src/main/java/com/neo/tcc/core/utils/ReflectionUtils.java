package com.neo.tcc.core.utils;

import java.lang.reflect.Method;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/3 18:27
 * @Description: 反射工具类
 */
public class ReflectionUtils {
    public static Class getDeclaringType(Class clazz, String methodName, Class<?>[] parameterTypes) {
        Method method = null;
        Class findClass = clazz;
        do {
            Class[] clazzes = findClass.getInterfaces();
            for (Class c :clazzes) {
                try {
                    method = c.getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException e) {
                    method = null;
                }
                if (method != null) {
                    return c;
                }
            }
            findClass = findClass.getSuperclass();
        } while (!findClass.equals(Object.class));
        return clazz;
    }

    public static Object getNullValue(Class type) {
        // 处理基本类型
        if (boolean.class.equals(type)) {
            return false;
        } else if (byte.class.equals(type)) {
            return 0;
        } else if (short.class.equals(type)) {
            return 0;
        } else if (int.class.equals(type)) {
            return 0;
        } else if (long.class.equals(type)) {
            return 0;
        } else if (float.class.equals(type)) {
            return 0;
        } else if (double.class.equals(type)) {
            return 0;
        } else if (char.class.equals(type)) {
            return ' ';
        }
        // 处理对象
        return null;
    }
}
