package com.neo.tcc.core.recover;

import com.alibaba.fastjson.JSON;
import com.neo.tcc.api.TransactionStatus;
import com.neo.tcc.core.OptimisticLockException;
import com.neo.tcc.core.Transaction;
import com.neo.tcc.core.TransactionRepository;
import com.neo.tcc.core.common.TransactionType;
import com.neo.tcc.core.support.TransactionConfigurator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/7 16:24
 * @Description: 异常事务恢复
 */
public class TransactionRecovery {
    private static final Logger log = LoggerFactory.getLogger(TransactionRecovery.class);

    /**
     * 事务配置器
     */
    private TransactionConfigurator transactionConfigurator;

    /**
     * 启动恢复事务逻辑
     */
    public void startRecover() {
        // 加载异常事务集合
        List<Transaction> transactions = loadErrorTransactions();
        // 恢复异常事务集合
        recoverErrorTransactions(transactions);
    }

    /**
     * 加载异常事务集合
     * 异常的定义：超过事务恢复间隔时间未完成的事务
     *
     * @return 异常事务集合
     */
    private List<Transaction> loadErrorTransactions() {
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        RecoverConfig recoverConfig = transactionConfigurator.getRecoverConfig();
        return transactionRepository.getTimeoutTransactions(new Date(currentTimeInMillis - recoverConfig.getRecoverDuration() * 1000));
    }

    /**
     * 恢复异常事务集合
     *
     * @param transactions 异常事务集合
     */
    private void recoverErrorTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            // 超过最大重试次数
            if (transaction.getRetryTimes() > transactionConfigurator.getRecoverConfig().getMaxRetryTimes()) {
                log.error("recover failed with max retry count, will not try again. tcc id:{}, status:{}, retry times:{}, transaction content:{}",
                        transaction.getId(),
                        transaction.getStatus().getId(),
                        transaction.getRetryTimes(),
                        JSON.toJSONString(transaction));
                continue;
            }
            // 分支事务超过最大可重试时间
            if (transaction.getTransactionType().equals(TransactionType.BRANCH)
                    && (transaction.getCreateTime().getTime() +
                    transactionConfigurator.getRecoverConfig().getMaxRetryTimes() *
                            transactionConfigurator.getRecoverConfig().getRecoverDuration() * 1000
                    > System.currentTimeMillis())) {
                continue;
            }
            // Confirm or Cancel
            try {
                // 增加重试次数
                transaction.addRetryTimes();
                if (transaction.getStatus().equals(TransactionStatus.CONFIRMING)) {
                    // Confirm
                    transaction.changeStatus(TransactionStatus.CONFIRMING);
                    transactionConfigurator.getTransactionRepository().update(transaction);
                    transaction.commit();
                    transactionConfigurator.getTransactionRepository().delete(transaction);
                } else if (transaction.getStatus().equals(TransactionStatus.CANCELLING) || transaction.getTransactionType().equals(TransactionType.ROOT)) {
                    // Cancel
                    // 处理延迟取消的情况
                    transaction.changeStatus(TransactionStatus.CANCELLING);
                    transactionConfigurator.getTransactionRepository().update(transaction);
                    transaction.rollback();
                    transactionConfigurator.getTransactionRepository().delete(transaction);
                }
            } catch (Throwable throwable) {
                if (throwable instanceof OptimisticLockException || ExceptionUtils.getRootCause(throwable) instanceof OptimisticLockException) {
                    log.warn("optimisticLockException happend while recover. tcc id:{}, status:{}, retry times:{}, transaction content:{}",
                            transaction.getId(),
                            transaction.getStatus().getId(),
                            transaction.getRetryTimes(),
                            JSON.toJSONString(transaction));
                } else {
                    log.error("recover failed, tcc id:{}, status:{}, retry time:{}, transaction content:{}",
                            transaction.getId(),
                            transaction.getStatus().getId(),
                            transaction.getRetryTimes(),
                            JSON.toJSONString(transaction));
                }
            }
        }
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }
}
