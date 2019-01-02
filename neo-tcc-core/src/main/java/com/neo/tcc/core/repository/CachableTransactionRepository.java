package com.neo.tcc.core.repository;

import com.google.common.cache.Cache;
import com.neo.tcc.core.Transaction;
import com.neo.tcc.core.TransactionRepository;
import com.neo.tcc.core.api.TransactionId;

import javax.transaction.xa.Xid;
import java.util.Date;
import java.util.List;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:45
 * @Description:
 */
public abstract class CachableTransactionRepository implements TransactionRepository {
    private int expire = 120;
    private Cache<Xid, Transaction> transactionCache;

    @Override
    public int create(Transaction transaction) {
        int result = doCreate(transaction);
        if (result > 0) {
            putToCache(transaction);
        }
        return result;
    }

    @Override
    public int update(Transaction transaction) {
        return 0;
    }

    @Override
    public int delete(Transaction transaction) {
        return 0;
    }

    @Override
    public Transaction getTransaction(TransactionId id) {
        return null;
    }

    @Override
    public List<Transaction> getTimeoutTransactions(Date date) {
        return null;
    }

    protected void putToCache(Transaction transaction) {
        transactionCache.put(transaction.getId(), transaction);
    }

    protected abstract int doCreate(Transaction transaction);

    protected abstract int doUpdate(Transaction transaction);

    protected abstract int doDelete(Transaction transaction);

    protected abstract Transaction doGetTransaction(Transaction transaction);

    protected abstract List<Transaction> doGetTimeoutTransactions(Date date);
}
