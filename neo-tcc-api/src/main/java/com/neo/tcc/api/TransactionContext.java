package com.neo.tcc.api;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 17:41
 * @Description: 事务上下文
 */
public class TransactionContext implements Serializable {
    private TransactionId id;
    private int status;
    private Map<String, String> attachments = new ConcurrentHashMap<>();

    public TransactionContext(TransactionId id) {
        this.id = id;
    }

    public TransactionContext(TransactionId id, int status) {
        this.id = id;
        this.status = status;
    }

    public TransactionId getId() {
        return id.clone();
    }

    public void setId(TransactionId id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        if (attachments != null && !attachments.isEmpty()) {
            this.attachments.putAll(attachments);
        }
    }
}
