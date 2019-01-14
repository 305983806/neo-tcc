package com.neo.tcc.core.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.neo.tcc.api.TransactionId;
import com.neo.tcc.core.OptimisticLockException;
import com.neo.tcc.core.Transaction;
import com.neo.tcc.core.TransactionRepository;

import javax.transaction.xa.Xid;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:45
 * @Description:
 */
public abstract class CachableTransactionRepository implements TransactionRepository {
    private int expire = 120;
    private Cache<Xid, Transaction> transactionCache;

    public CachableTransactionRepository() {
        this.transactionCache = CacheBuilder.newBuilder().expireAfterAccess(expire, TimeUnit.SECONDS).maximumSize
                (1000).build();
    }

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
        int result = 0;
        try {
            result = doUpdate(transaction);
            if (result > 0) {
                putToCache(transaction);
            } else {
                throw new OptimisticLockException();
            }
        } finally {
            if (result <= 0) {
                removeFromCache(transaction);
            }
        }
        return result;
    }

    @Override
    public int delete(Transaction transaction) {
        int result = 0;
        try {
            result = doDelete(transaction);
        } finally {
            removeFromCache(transaction);
        }
        return result;
    }

    @Override
    public Transaction getTransaction(TransactionId transactionId) {
        Transaction transaction = findFromCache(transactionId);
        if (transaction == null) {
            transaction = doGetTransaction(transactionId);
            if (transaction != null) {
                putToCache(transaction);
            }
        }
        return transaction;
    }

    @Override
    public List<Transaction> getTimeoutTransactions(Date date) {
        List<Transaction> transactions = doGetTimeoutTransactions(date);
        for (Transaction transaction : transactions) {
            putToCache(transaction);
        }
        return transactions;
    }

    protected void putToCache(Transaction transaction) {
        transactionCache.put(transaction.getId(), transaction);
    }

    protected void removeFromCache(Transaction transaction) {
        transactionCache.invalidate(transaction.getId());
    }

    protected Transaction findFromCache(TransactionId transactionId) {
        return transactionCache.getIfPresent(transactionId);
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    protected abstract int doCreate(Transaction transaction);

    protected abstract int doUpdate(Transaction transaction);

    protected abstract int doDelete(Transaction transaction);

    protected abstract Transaction doGetTransaction(Xid xid);

    protected abstract List<Transaction> doGetTimeoutTransactions(Date date);
}
