package com.neo.tcc.spring.recover;

import com.neo.tcc.core.SystemException;
import com.neo.tcc.core.recover.TransactionRecovery;
import com.neo.tcc.core.support.TransactionConfigurator;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/8 18:17
 * @Description:
 */
public class RecoverScheduledJob {
    private TransactionRecovery transactionRecovery;
    private TransactionConfigurator transactionConfigurator;
    private Scheduler scheduler;

    public void init() {
        try {
            MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
            jobDetail.setTargetObject(transactionRecovery);
            jobDetail.setTargetMethod("startRecover");
            jobDetail.setName("transactionRecoveryJob");
            jobDetail.setConcurrent(false);
            jobDetail.afterPropertiesSet();

            CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
            cronTrigger.setBeanName("transactionRecoveryCronTrigger");
            cronTrigger.setCronExpression(transactionConfigurator.getRecoverConfig().getCronExpression());
            cronTrigger.setJobDetail(jobDetail.getObject());
            cronTrigger.afterPropertiesSet();

            scheduler.scheduleJob(jobDetail.getObject(), cronTrigger.getObject());
            scheduler.start();
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    public void setTransactionRecovery(TransactionRecovery transactionRecovery) {
        this.transactionRecovery = transactionRecovery;
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
