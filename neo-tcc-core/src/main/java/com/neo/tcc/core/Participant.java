package com.neo.tcc.core;

import com.neo.tcc.core.api.TransactionId;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:39
 * @Description:
 */
public class Participant {
    private TransactionId id;
    private InvocationContext confirmInvocationContext;
    private InvocationContext cancelInvocationContext;
    private Terminator terminator;

    public void commit() {

    }

    public void rollback() {

    }
}
