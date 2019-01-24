package com.neo.tcc.spring.config;

import com.neo.tcc.core.recover.TransactionRecovery;
import com.neo.tcc.spring.ConfigurableCoordinatorAspect;
import com.neo.tcc.spring.ConfigurableTransactionAspect;
import com.neo.tcc.spring.recover.RecoverScheduledJob;
import com.neo.tcc.spring.support.SpringBeanFactory;
import com.neo.tcc.spring.support.SpringTransactionConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/8 21:11
 * @Description:
 */
@Component
@EnableScheduling
public class TCCSpringConfiguration {
    @Bean
    public SpringBeanFactory springBeanFactory() {
        return new SpringBeanFactory();
    }

    @Bean(initMethod = "init")
    public SpringTransactionConfigurator transactionConfigurator() {
        SpringTransactionConfigurator configurator = new SpringTransactionConfigurator();
        return configurator;
    }

    @Bean(initMethod = "init")
    public ConfigurableTransactionAspect compensableTransactionAspect(SpringTransactionConfigurator transactionConfigurator) {
        ConfigurableTransactionAspect aspect = new ConfigurableTransactionAspect();
        aspect.setTransactionConfigurator(transactionConfigurator);
        return aspect;
    }

    @Bean(initMethod = "init")
    public ConfigurableCoordinatorAspect resourceCoordinatorAspect(SpringTransactionConfigurator transactionConfigurator) {
        ConfigurableCoordinatorAspect aspect = new ConfigurableCoordinatorAspect();
        aspect.setTransactionConfigurator(transactionConfigurator);
        return aspect;
    }

    @Bean
    public TransactionRecovery transactionRecovery(SpringTransactionConfigurator transactionConfigurator) {
        TransactionRecovery recovery = new TransactionRecovery();
        recovery.setTransactionConfigurator(transactionConfigurator);
        return recovery;
    }

    @Bean
    public SchedulerFactoryBean recoverScheduler() {
        return new SchedulerFactoryBean();
    }

    @Bean(initMethod = "init")
    public RecoverScheduledJob recoverScheduledJob(TransactionRecovery transactionRecovery,
                                                   SpringTransactionConfigurator transactionConfigurator,
                                                   SchedulerFactoryBean recoverScheduler) {
        RecoverScheduledJob job = new RecoverScheduledJob();
        job.setTransactionRecovery(transactionRecovery);
        job.setTransactionConfigurator(transactionConfigurator);
        job.setScheduler(recoverScheduler.getScheduler());
        return job;
    }
}
