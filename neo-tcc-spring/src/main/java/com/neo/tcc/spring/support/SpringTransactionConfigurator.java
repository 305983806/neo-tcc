package com.neo.tcc.spring.support;

import com.neo.tcc.core.TransactionManager;
import com.neo.tcc.core.TransactionRepository;
import com.neo.tcc.core.recover.RecoverConfig;
import com.neo.tcc.core.repository.CachableTransactionRepository;
import com.neo.tcc.core.support.TransactionConfigurator;
import com.neo.tcc.spring.recover.DefaultRecoverConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/8 18:07
 * @Description:
 */
public class SpringTransactionConfigurator implements TransactionConfigurator {

    private static volatile ExecutorService executorService = null;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired(required = false)
    private RecoverConfig recoverConfig = DefaultRecoverConfig.INSTANCE;

    private TransactionManager transactionManager;

    public void init() {
        transactionManager = new TransactionManager();
        transactionManager.setTransactionRepository(transactionRepository);

        if (executorService == null) {
            synchronized (SpringTransactionConfigurator.class) {
                if (executorService == null) {
                    executorService = Executors.newCachedThreadPool();
                }
            }
        }
        transactionManager.setExecutorService(executorService);

        if (transactionRepository instanceof CachableTransactionRepository) {
            ((CachableTransactionRepository) transactionRepository).setExpire(recoverConfig.getRecoverDuration());
        }
    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    @Override
    public RecoverConfig getRecoverConfig() {
        return recoverConfig;
    }
}
