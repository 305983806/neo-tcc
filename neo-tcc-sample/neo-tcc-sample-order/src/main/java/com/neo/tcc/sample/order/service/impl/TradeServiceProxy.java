package com.neo.tcc.sample.order.service.impl;

import com.neo.rpc.client.RpcClient;
import com.neo.tcc.api.Compensable;
import com.neo.tcc.api.Propagation;
import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.core.context.MethodTransactionContextEditor;
import com.neo.tcc.sample.inventory.api.InventoryService;
import com.neo.tcc.sample.member.api.BonuspointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TradeServiceProxy {
    @Autowired
    private RpcClient rpcClient;

    @Compensable(propagation = Propagation.SUPPORTS, confirmMethod = "useInv", cancelMethod = "useInv", transactionContextEditor = MethodTransactionContextEditor.class)
    public void useInv (TransactionContext transactionContext) {
        InventoryService inv = rpcClient.create(InventoryService.class);
        inv.preUse(transactionContext);
    }

    public void useBonuspoint(TransactionContext transactionContext) {
        BonuspointService bonuspointService = rpcClient.create(BonuspointService.class);
        bonuspointService.preUse(transactionContext);
    }
}
