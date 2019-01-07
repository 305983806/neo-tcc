package com.neo.tcc.core;

import com.neo.tcc.core.api.TransactionId;

import java.util.Date;
import java.util.List;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:44
 * @Description: 事务存储器
 */
public interface TransactionRepository {
    /**
     * 新增事务
     *
     * @param transaction 事务
     * @return 新增数量
     */
    int create(Transaction transaction);
    int update(Transaction transaction);
    int delete(Transaction transaction);
    Transaction getTransaction(TransactionId transactionId);
    List<Transaction> getTimeoutTransactions(Date date);
}