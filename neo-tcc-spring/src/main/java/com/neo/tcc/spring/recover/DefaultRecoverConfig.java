package com.neo.tcc.spring.recover;

import com.neo.tcc.core.OptimisticLockException;
import com.neo.tcc.core.recover.RecoverConfig;

import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/8 18:10
 * @Description:
 */
public class DefaultRecoverConfig implements RecoverConfig {
    public static final RecoverConfig INSTANCE = new DefaultRecoverConfig();

    private int maxRetryCount = 5;
    private int recoverDuration = 120; // 秒
    private String cronExpression = "0 */1 * * * ?";
    private int asyncTerminateThreadPoolSize = 1024;
    private Set<Class<? extends Exception>> delayCancelExceptions = new HashSet<>();

    public DefaultRecoverConfig() {
        delayCancelExceptions.add(OptimisticLockException.class);
        delayCancelExceptions.add(SocketTimeoutException.class);
    }

    @Override
    public int getMaxRetryTimes() {
        return maxRetryCount;
    }

    @Override
    public int getRecoverDuration() {
        return recoverDuration;
    }

    @Override
    public String getCronExpression() {
        return cronExpression;
    }

    @Override
    public Set<Class<? extends Exception>> getDelayCancelExceptions() {
        return this.delayCancelExceptions;
    }

    @Override
    public int getAsyncTerminateThreadPoolsize() {
        return asyncTerminateThreadPoolSize;
    }

    @Override
    public void setDelayCancelExceptions(Set<Class<? extends Exception>> delayCancelExceptions) {
        this.delayCancelExceptions.addAll(delayCancelExceptions);
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public void setRecoverDuration(int recoverDuration) {
        this.recoverDuration = recoverDuration;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public void setAsyncTerminateThreadPoolSize(int asyncTerminateThreadPoolSize) {
        this.asyncTerminateThreadPoolSize = asyncTerminateThreadPoolSize;
    }
}
