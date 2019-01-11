package com.neo.tcc.spring.config;

import com.neo.tcc.core.recover.TransactionRecovery;
import com.neo.tcc.spring.ConfigurableTransactionAspect;
import com.neo.tcc.spring.recover.RecoverScheduledJob;
import com.neo.tcc.spring.support.SpringBeanFactory;
import com.neo.tcc.spring.support.SpringTransactionConfigurator;
import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/8 21:11
 * @Description:
 */
//@Component
//@EnableScheduling
public class TCCSpringConfiguration {
    @Bean
    public SpringBeanFactory springBeanFactory() {
        return new SpringBeanFactory();
    }

    @Bean
    public SpringTransactionConfigurator transactionConfigurator() {
        SpringTransactionConfigurator configurator = new SpringTransactionConfigurator();
        configurator.init();
        return configurator;
    }

    @Bean
    public ConfigurableTransactionAspect compensableTransactionAspect(SpringTransactionConfigurator transactionConfigurator) {
        ConfigurableTransactionAspect aspect = new ConfigurableTransactionAspect();
        aspect.setTransactionConfigurator(transactionConfigurator);
        aspect.init();
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

    @Bean
    public RecoverScheduledJob recoverScheduledJob(TransactionRecovery transactionRecovery,
                                                   SpringTransactionConfigurator transactionConfigurator) {
        RecoverScheduledJob job = new RecoverScheduledJob();
        job.setTransactionRecovery(transactionRecovery);
        job.setTransactionConfigurator(transactionConfigurator);
        job.setScheduler((Scheduler) recoverScheduler());
        return job;
    }
}
