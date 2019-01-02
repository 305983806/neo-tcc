package com.neo.tcc.core;

import com.neo.tcc.core.api.TransactionContext;
import com.neo.tcc.core.common.TransactionStatus;
import com.neo.tcc.core.common.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:44
 * @Description:
 */
public class TransactionManager {
    private static final Logger log = LoggerFactory.getLogger(TransactionManager.class);

    private TransactionRepository transactionRepository;
    private ThreadLocal<Deque<Transaction>> CURRENT = new ThreadLocal<Deque<Transaction>>();

    private ExecutorService executorService;

    /**
     * 开启主事务
     * @return 事务对象
     */
    public Transaction begin() {
        // 创建并存储主事务
        Transaction transaction = new Transaction(TransactionType.ROOT);
        // 存储事务
        transactionRepository.create(transaction);
        // 将主事务注册到当前线程的事务队列
        registerTransaction(transaction);
        return transaction;
    }

    /**
     * 传播新的分支事务
     * 用于从一个事务上下文当中传播一个新的事务，通常会在分支事务的 try 阶段被调用，此处的事务上下文使用方法传参
     *
     * @param context 事务上下文
     * @return 分支事务对象
     */
    public Transaction propagationNewBegin(TransactionContext context) {
        Transaction transaction = new Transaction(context);
        transactionRepository.create(transaction);
        registerTransaction(transaction);
        return transaction;
    }

    /**
     * 传播已有分支事务
     * 用于从事务上下文中传播一个已存在的事务，通常会在分支事务的 confirm 阶段和 cancel 阶段被调用，此处的事务上下文通过方法传参
     *
     * @param context
     * @return
     */
    public Transaction PropagationExistBegin(TransactionContext context) throws NoExistedTransactionException {
        // 查询事务
        Transaction transaction = transactionRepository.getTransaction(context.getId());
        if (transaction != null) {
            // 设置事务状态
            transaction.changeStatus(TransactionStatus.valueOf(context.getStatus()));
            registerTransaction(transaction);
            return transaction;
        } else {
            throw new NoExistedTransactionException();
        }
    }

    /**
     * 提交事务
     * 在事务 try 阶段没有异常的情况下，由 TCC 框架自动调用。它首先从 ThreadLocal 队列中取出当前要处理的事务（但不删除），然后将事务
     * 状态改为 CONFIRMING 状态，更新事务日志。随后调用事务的 commit 方法进行事务提交，如果事务提交成功（没有抛出任何异常），那么就从
     * 事务日志仓库中删除这个事务日志。如果在事务 commit 过程中抛出了异常，那么这个事务日志此时不会被删除（稍后会被 recovery 任务处理），
     * 同时，框架会将异常全部转为 ConfirmingException 向业务层抛出。
     *
     * @param asyncCommit 是否异步提交
     */
    public void commit(boolean asyncCommit) {
        // 获取本地线程上事务队列中的时间最久的事务
        Transaction transaction = getCurrentTransaction();
        // 设置事务状态为 CONFIRMING
        transaction.changeStatus(TransactionStatus.CONFIRMING);
        // 更新事务日志
        transactionRepository.update(transaction);

        if (asyncCommit) {
            try {
                Long statTime = System.currentTimeMillis();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        commitTransaction(transaction);
                    }
                });
                log.debug("async submit cost time" + (System.currentTimeMillis() - statTime));
            } catch (Throwable commitException) {
                // 当事务提交过程中出现异常，转抛出 ConfirmingException 异常，且事务在事务日志中不被删除，交由 recovery 去处理长时间没有被删除的事务
                log.warn("compensable transaction async submit confirm failed, recovery job will try to confirm later.", commitException);
                throw new ConfirmingException(commitException);
            }
        } else {
            commitTransaction(transaction);
        }
    }

    private void commitTransaction(Transaction transaction) {
        try {
            transaction.commit();
            transactionRepository.delete(transaction);
        } catch (Throwable commitException) {
            log.warn("compensable transaction async submit confirm failed, recovery job will try to confirm later.", commitException);
            throw new ConfirmingException(commitException);
        }
    }

    public void rollback() {}

    public void addParticipant() {}

    /**
     * 获取当前线程事务第一个（头部）元素
     *
     * @return
     */
    public Transaction getCurrentTransaction() {
        if (isTransactionActive()) {
            return CURRENT.get().peek(); // 获得头部元素
        }
        return null;
    }

    /**
     * 当前线程是否在事务中
     *
     * @return 是否在事务中
     */
    public boolean isTransactionActive() {
        Deque<Transaction> transactions = CURRENT.get();
        return transactions != null && !transactions.isEmpty();
    }

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

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
