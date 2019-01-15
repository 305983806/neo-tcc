package com.neo.tcc.core.utils;

import java.util.Collection;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/15 14:44
 * @Description:
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection collection) {
        return (collection == null || collection.isEmpty());
    }
}
