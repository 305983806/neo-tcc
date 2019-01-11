package com.neo.tcc.api;

import java.io.Serializable;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 17:41
 * @Description: 事务上下文
 */
public class TransactionContext implements Serializable {
    private TransactionId id;
    private int status;

    public TransactionContext(TransactionId id) {
        this.id = id;
    }

    public TransactionContext(TransactionId id, int status) {
        this.id = id;
        this.status = status;
    }

    public TransactionId getId() {
        return id;
    }

    public void setId(TransactionId id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
