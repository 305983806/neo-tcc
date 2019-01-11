package com.neo.tcc.sample.order.service.impl;

import com.neo.tcc.api.Compensable;
import com.neo.tcc.api.Propagation;
import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.core.context.MethodTransactionContextEditor;
import com.neo.tcc.sample.inventory.api.InventoryService;
import com.neo.tcc.sample.inventory.bean.InvUse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/11 16:44
 * @Description:
 */
@Component
public class InventoryServiceProxy {
    @Autowired
    private InventoryService inventoryService;

    @Compensable(propagation = Propagation.SUPPORTS, confirmMethod = "doSubmit", cancelMethod = "cancelSubmit",
            transactionContextEditor = MethodTransactionContextEditor.class)
    protected void use(TransactionContext transactionContext, InvUse invUse) {
        inventoryService.getInvService().Use(transactionContext, invUse);
    }
}
