package com.neo.tcc.core;

import com.neo.tcc.core.common.TransactionType;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:44
 * @Description:
 */
public class TransactionManager {
    private TransactionRepository transactionRepository;
    private ThreadLocal<Deque<Transaction>> CURRENT = new ThreadLocal<Deque<Transaction>>();

    /**
     * 开启主事务
     * @return
     */
    public Transaction begin() {
        // 创建并存储主事务
        Transaction transaction = new Transaction(TransactionType.ROOT);
        transactionRepository.create(transaction);
        // 将主事务注册到当前线程的事务队列
        registerTransaction(transaction);
        return transaction;
    }

    public void commit() {

    }

    public void rollback() {}

    public void addParticipant() {}

    /**
     * 注册事务到当前线程事务队列
     * 由于需要支持多个事务独立存在，后创建先提交，因此使用了LinkedList
     * @param transaction
     */
    public void registerTransaction(Transaction transaction) {
        if (CURRENT.get() == null) {
            CURRENT.set(new LinkedList<>());
        }
        CURRENT.get().push(transaction);
    }
}
