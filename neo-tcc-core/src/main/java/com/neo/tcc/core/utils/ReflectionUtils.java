package com.neo.tcc.core.utils;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/3 18:27
 * @Description: 反射工具类
 */
public class ReflectionUtils {
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
