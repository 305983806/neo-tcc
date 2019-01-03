package com.neo.tcc.core;

import com.neo.tcc.core.api.TransactionContext;
import com.neo.tcc.core.api.TransactionContextEditor;
import com.neo.tcc.core.api.TransactionId;
import com.neo.tcc.core.common.TransactionStatus;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:39
 * @Description:
 */
public class Participant {
    /**
     * 事务编号
     */
    private TransactionId id;
    /**
     * 确认执行业务方法的上下文
     */
    private InvocationContext confirmInvocationContext;
    /**
     * 取消执行业务方法的上下文
     */
    private InvocationContext cancelInvocationContext;
    /**
     * 执行器
     */
    private Terminator terminator = new Terminator();

    /**
     * 事务上下文编辑器
     */
    Class<? extends TransactionContextEditor> transactionContextEditorClass;

    /**
     * 提交事务
     */
    public void commit() {
        terminator.invoke(new TransactionContext(id, TransactionStatus.CONFIRMING.getId()), confirmInvocationContext, transactionContextEditorClass);
    }

    /**
     * 回滚事务
     */
    public void rollback() {
        terminator.invoke(new TransactionContext(id, TransactionStatus.CANCELLING.getId()), cancelInvocationContext, transactionContextEditorClass);
    }

    public TransactionId getId() {
        return id;
    }

    public void setId(TransactionId id) {
        this.id = id;
    }

    public InvocationContext getConfirmInvocationContext() {
        return confirmInvocationContext;
    }

    public InvocationContext getCancelInvocationContext() {
        return cancelInvocationContext;
    }

    public Terminator getTerminator() {
        return terminator;
    }
}
