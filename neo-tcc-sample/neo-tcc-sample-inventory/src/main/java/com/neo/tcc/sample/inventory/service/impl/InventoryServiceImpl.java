package com.neo.tcc.sample.inventory.service.impl;

import com.neo.rpc.server.RpcService;
import com.neo.tcc.api.Compensable;
import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.core.context.MethodTransactionContextEditor;
import com.neo.tcc.sample.inventory.api.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/11 17:13
 * @Description:
 */
//@Service
@RpcService(InventoryService.class)
public class InventoryServiceImpl implements InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Autowired
    private InventoryTCCService inventoryTCCService;

    @Override
    public void preUse(TransactionContext transactionContext) {
        inventoryTCCService.preUse(transactionContext);
    }

    @Override
    public void confirmUse(TransactionContext transactionContext) {

    }

    @Override
    public void cancelUse(TransactionContext transactionContext) {

    }
}
