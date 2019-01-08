package com.neo.tcc.spring;

import com.neo.tcc.core.TransactionManager;
import com.neo.tcc.core.interceptor.CompensableTransactionAspect;
import com.neo.tcc.core.interceptor.CompensableTransactionInterceptor;
import com.neo.tcc.core.support.TransactionConfigurator;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/8 14:19
 * @Description:
 */
@Aspect
public class ConfigurableTransactionAspect extends CompensableTransactionAspect implements Ordered {
    private TransactionConfigurator transactionConfigurator;

    public void init() {
        TransactionManager transactionManager = transactionConfigurator.getTransactionManager();
        CompensableTransactionInterceptor compensableTransactionInterceptor = new CompensableTransactionInterceptor();
        compensableTransactionInterceptor.setTransactionManager(transactionManager);
        compensableTransactionInterceptor.setDelayCancelExceptions(transactionConfigurator.getRecoverConfig().getDelayCancelExceptions());
        this.setCompensableTransactionInterceptor(compensableTransactionInterceptor);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }
}
