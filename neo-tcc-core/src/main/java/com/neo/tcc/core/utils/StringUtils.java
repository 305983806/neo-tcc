package com.neo.tcc.core.utils;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 22:09
 * @Description:
 */
public class StringUtils {
    public static boolean isNotEmpty(String value) {
        if (value == null) {
            return false;
        }
        if (value.equals("")) {
            return false;
        }
        return true;
    }
}
