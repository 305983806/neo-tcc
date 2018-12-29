package com.neo.tcc.core.common;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:43
 * @Description:
 */
public enum TransactionType {
    ROOT(1),
    BRANCH(2),
    ;

    int id;

    TransactionType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TransactionType valueOf(int id) {
        switch (id) {
            case 1:
                return ROOT;
            case 2:
                return BRANCH;
            default:
                return null;
        }
    }
}
