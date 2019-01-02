package com.neo.tcc.core;

import com.neo.tcc.core.api.TransactionId;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:39
 * @Description:
 */
public class Participant {
    /**
     * 事务编号
     */
    private TransactionId id;
    /**
     * 确认执行业务方法的上下文
     */
    private InvocationContext confirmInvocationContext;
    /**
     * 取消执行业务方法的上下文
     */
    private InvocationContext cancelInvocationContext;
    /**
     * 执行器
     */
    private Terminator terminator = new Terminator();



    public void commit() {

    }

    public void rollback() {

    }
}
