package com.neo.tcc.api;

import javax.transaction.xa.Xid;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:39
 * @Description:
 */
public class TransactionId implements Xid, Serializable {
    private int formatId = 1;
    private byte[] globalTransactionId;
    private byte[] branchQualifier;

    public TransactionId() {
        globalTransactionId = uuidToByteArray(UUID.randomUUID());
        branchQualifier = uuidToByteArray(UUID.randomUUID());
    }

    public TransactionId(byte[] globalTransactionId) {
        this.globalTransactionId = globalTransactionId;
        branchQualifier = uuidToByteArray(UUID.randomUUID());
    }

    public TransactionId(byte[] globalTransactionId, byte[] branchQualifier) {
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
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

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        TransactionId other = (TransactionId) obj;
        if (this.getFormatId() != other.getFormatId()) {
            return false;
        } else if (!Arrays.equals(branchQualifier, other.branchQualifier)) {
            return false;
        } else if (!Arrays.equals(globalTransactionId, other.globalTransactionId)) {
            return false;
        }
        return true;
    }

    private static byte[] uuidToByteArray(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    private static UUID byteArrayToUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    @Override
    public int getFormatId() {
        return formatId;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return globalTransactionId;
    }

    @Override
    public byte[] getBranchQualifier() {
        return branchQualifier;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(UUID.nameUUIDFromBytes(globalTransactionId).toString());
        stringBuilder.append(":").append(UUID.nameUUIDFromBytes(branchQualifier).toString());
        return stringBuilder.toString();
    }
}