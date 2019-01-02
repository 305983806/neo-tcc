package com.neo.tcc.core;

import com.neo.tcc.core.api.TransactionContext;
import com.neo.tcc.core.api.TransactionId;
import com.neo.tcc.core.common.TransactionStatus;
import com.neo.tcc.core.common.TransactionType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:39
 * @Description:
 */
public class Transaction {
    private TransactionId id;
    private TransactionStatus status;
    private TransactionType transactionType;
    private int retryTime;
    private Date createTime;
    private Date lastUpdateTime;
    private long version;
    private List<Participant> participants = new ArrayList<>();

    public Transaction(TransactionContext transactionContext) {
        this.id = transactionContext.getId();
        this.status = TransactionStatus.TRYING;
        this.transactionType = TransactionType.BRANCH;
    }

    public Transaction(TransactionType transactionType) {
        this.id = new TransactionId();
        this.status = TransactionStatus.TRYING;
        this.transactionType = transactionType;
    }

    public void changeStatus(TransactionStatus status) {
        this.status = status;
    }

    public void resetRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }

    public void setLastUpdateTime(Date date) {
        this.lastUpdateTime = date;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void commit() {}

    public void rollback() {}

    public void addParticipant() {}

    public TransactionId getId() {
        return id.clone();
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public long getVersion() {
        return version;
    }

    public List<Participant> getParticipants() {
        return participants;
    }
}
