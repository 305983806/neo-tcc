package com.neo.tcc.core.repository;

import com.neo.tcc.core.Transaction;

import java.util.Date;
import java.util.List;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:45
 * @Description:
 */
public class RedisTransactionRepository extends CachableTransactionRepository {
    @Override
    protected int doCreate(Transaction transaction) {
        return 0;
    }

    @Override
    protected int doUpdate(Transaction transaction) {
        return 0;
    }

    @Override
    protected int doDelete(Transaction transaction) {
        return 0;
    }

    @Override
    protected Transaction doGetTransaction(Transaction transaction) {
        return null;
    }

    @Override
    protected List<Transaction> doGetTimeoutTransactions(Date date) {
        return null;
    }
}
