package com.neo.tcc.core.utils;

import com.neo.tcc.core.api.Propagation;
import com.neo.tcc.core.api.TransactionContext;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/3 17:04
 * @Description: 事务工具类
 */
public class TransactionUtils {

    /**
     * 判断事务上下文是否合法
     * 在 Propagation.MANDATORY 必须有在事务内
     *
     * @param isTransactionActive 是否开启事务
     * @param propagation 传播级别
     * @param context 事务上下文
     * @return 是否合法
     */
    public static boolean isLegalTransactionContext(boolean isTransactionActive, Propagation propagation, TransactionContext context) {
        if (propagation.equals(Propagation.MANDATORY) && !isTransactionActive && context ==null) {
            return false;
        }
        return true;
    }
}
