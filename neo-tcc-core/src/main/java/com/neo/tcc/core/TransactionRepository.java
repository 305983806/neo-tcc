package com.neo.tcc.core;

import com.neo.tcc.core.api.TransactionId;

import java.util.Date;
import java.util.List;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:44
 * @Description:
 */
public interface TransactionRepository {
    int create(Transaction transaction);
    int update(Transaction transaction);
    int delete(Transaction transaction);
    Transaction getTransaction(TransactionId id);
    List<Transaction> getTimeoutTransactions(Date date);
}
