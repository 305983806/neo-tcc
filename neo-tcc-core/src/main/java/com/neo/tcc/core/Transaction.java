package com.neo.tcc.core;

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

    public Transaction(TransactionType transactionType) {
        this.id = new TransactionId();
        this.status = TransactionStatus.TRYING;
        this.transactionType = transactionType;
    }

    public void commit() {}

    public void rollback() {}

    public void addParticipant() {}
}
