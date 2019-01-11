package com.neo.tcc.api;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:42
 * @Description:
 */
public enum TransactionStatus {
    TRYING(1),
    CONFIRMING(2),
    CANCELLING(3),
    ;

    private int id;

    TransactionStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TransactionStatus valueOf(int id) {
        switch (id) {
            case 1:
                return TRYING;
            case 2:
                return CONFIRMING;
            default:
                return CANCELLING;
        }
    }
}
