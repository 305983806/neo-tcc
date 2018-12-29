package com.neo.tcc.core;

import java.util.Deque;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:44
 * @Description:
 */
public class TransactionManager {
    private TransactionRepository transactionRepository;
    private ThreadLocal<Deque<Transaction>> CURRENT = new ThreadLocal<Deque<Transaction>>();

    public Transaction begin() {
        return null;
    }

    public void commit() {

    }

    public void rollback() {}

    public void addParticipant() {}

    public void registerTransaction() {}
}
