package com.neo.tcc.core.recover;

import java.util.Set;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/7 16:13
 * @Description: 事务恢复配置接口
 */
public interface RecoverConfig {

    /**
     * @return 最大重试次数
     */
    int getMaxRetryCount();

    /**
     * @return 恢复间隔时间，单位：秒
     */
    int getRecoverDuration();

    /**
     * @return corn 表达式
     */
    String getCronExpression();

    /**
     * @return 延迟取消异常集合
     */
    Set<Class<? extends Exception>> getDelayCancelExceptions();

    /**
     * 设置延迟取消异常集合
     *
     * @param delayCancelExceptions 延迟取消异常集合
     */
    void setDelayCancelExceptions(Set<Class<? extends Exception>> delayCancelExceptions);


    /**
     * @return 异步终止线程池大小
     */
    int getAsyncTerminateThreadPoolsize();
}
