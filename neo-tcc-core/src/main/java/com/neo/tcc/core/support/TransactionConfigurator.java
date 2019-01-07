package com.neo.tcc.core.support;

import com.neo.tcc.core.TransactionManager;
import com.neo.tcc.core.TransactionRepository;
import com.neo.tcc.core.recover.RecoverConfig;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/7 16:25
 * @Description: 事务配置器
 */
public interface TransactionConfigurator {

    TransactionManager getTransactionManager();

    TransactionRepository getTransactionRepository();

    RecoverConfig getRecoverConfig();
}
