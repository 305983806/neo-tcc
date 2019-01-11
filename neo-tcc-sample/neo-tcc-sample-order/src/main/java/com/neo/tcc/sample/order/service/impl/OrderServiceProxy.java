package com.neo.tcc.sample.order.service.impl;

import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.sample.inventory.api.InventoryService;
import com.neo.tcc.sample.inventory.bean.InvUse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/10 20:44
 * @Description:
 */
@Component
public class OrderServiceProxy {
    @Autowired
    private InventoryService inventoryService;

    /**
     * 预占库存
     */
//    @Compensable(propagation = Propagation.SUPPORTS, confirmMethod = "doCreate", cancelMethod = "cancel",
//            transactionContextEditor = MethodTransactionContextEditor.class)
    public void holdingStock(TransactionContext transactionContext, InvUse invUse) {
        inventoryService.getInvService().preUse(transactionContext, invUse);
    }
}
