package com.neo.tcc.sample.inventory.api;

import com.neo.tcc.api.TransactionContext;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/22 16:14
 * @Description:
 */
public interface InventoryService {

    void preUse(TransactionContext transactionContext);

    void confirmUse(TransactionContext transactionContext);

    void cancelUse(TransactionContext transactionContext);
}
