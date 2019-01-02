package com.neo.tcc.core.api;

import javax.transaction.xa.Xid;
import java.io.Serializable;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:39
 * @Description:
 */
public class TransactionId implements Xid, Serializable {
    private byte[] globalTransactionId;
    private byte[] branchQualifier;

    public TransactionId() {}

    public TransactionId(byte[] globalTransactionId, byte[] branchQualifier) {
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
    }

    @Override
    public int getFormatId() {
        return 0;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return new byte[0];
    }

    @Override
    public byte[] getBranchQualifier() {
        return new byte[0];
    }

    public TransactionId clone() {
        byte[] cloneGlobalTransactionId = null;
        byte[] cloneBranchQualifier = null;

        if (globalTransactionId != null) {
            cloneGlobalTransactionId = new byte[globalTransactionId.length];
            System.arraycopy(globalTransactionId, 0, cloneGlobalTransactionId, 0, globalTransactionId.length);
        }

        if (branchQualifier != null) {
            cloneBranchQualifier = new byte[branchQualifier.length];
            System.arraycopy(branchQualifier, 0, cloneBranchQualifier, 0, branchQualifier.length);
        }

        return new TransactionId(cloneGlobalTransactionId, cloneBranchQualifier);
    }
}
