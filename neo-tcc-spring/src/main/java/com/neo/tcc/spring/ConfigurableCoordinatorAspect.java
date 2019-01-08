package com.neo.tcc.spring;

import com.neo.tcc.core.interceptor.ResourceCoordinatorAspect;
import com.neo.tcc.core.interceptor.ResourceCoordinatorInterceptor;
import com.neo.tcc.core.support.TransactionConfigurator;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/8 16:34
 * @Description:
 */
@Aspect
public class ConfigurableCoordinatorAspect extends ResourceCoordinatorAspect implements Ordered {
    private TransactionConfigurator transactionConfigurator;

    public void init() {
        ResourceCoordinatorInterceptor resourceCoordinatorInterceptor = new ResourceCoordinatorInterceptor();
        resourceCoordinatorInterceptor.setTransactionManager(transactionConfigurator.getTransactionManager());
        this.setResourceCoordinatorInterceptor(resourceCoordinatorInterceptor);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }
}
