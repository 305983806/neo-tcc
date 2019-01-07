package com.neo.tcc.core;

import com.neo.tcc.core.api.TransactionContext;
import com.neo.tcc.core.api.TransactionId;
import com.neo.tcc.core.api.TransactionStatus;
import com.neo.tcc.core.common.TransactionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:39
 * @Description:
 */
public class Transaction implements Serializable {
    /**
     * 事务编号
     */
    private TransactionId id;
    /**
     * 事务状态
     */
    private TransactionStatus status;
    /**
     * 事务类型
     */
    private TransactionType transactionType;
    /**
     * 重试次数
     */
    private int retryTimes;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;
    /**
     * 版本号
     */
    private long version = 1;
    /**
     * 参与者集合
     */
    private List<Participant> participants = new ArrayList<>();
    /**
     * 附带属性映射
     */
    private Map<String, Object> attachments = new ConcurrentHashMap<>();

    /**
     * [构造]创建分支事务
     *
     * @param transactionContext 事务上下文
     */
    public Transaction(TransactionContext transactionContext) {
        this.id = transactionContext.getId();
        this.status = TransactionStatus.TRYING;
        this.transactionType = TransactionType.BRANCH;
    }

    /**
     * 创建指定类型的事务
     *
     * @param transactionType 事务类型
     */
    public Transaction(TransactionType transactionType) {
        this.id = new TransactionId();
        this.status = TransactionStatus.TRYING;
        this.transactionType = transactionType;
    }

    public void changeStatus(TransactionStatus status) {
        this.status = status;
    }

    /**
     * 添加参与者
     *
     * @param participant 参与者
     */
    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    /**
     * 提交 TCC 事务
     */
    public void commit() {
        for (Participant participant: participants) {
            participant.commit();
        }
    }

    /**
     * 回滚 TCC 事务
     */
    public void rollback() {
        for (Participant participant : participants) {
            participant.rollback();
        }
    }

    public TransactionId getId() {
        return id.clone();
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void addRetryTimes() {
        this.retryTimes++;
    }

    public void resetRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date date) {
        this.lastUpdateTime = date;
    }

    public void updateTime() {
        this.lastUpdateTime = new Date();
    }

    public long getVersion() {
        return version;
    }

    public void updateVersion() {
        this.version++;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }
}
