package com.neo.tcc.sample.inventory.api.impl;

import com.lonntec.common.bean.result.SuccessResp;
import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.sample.inventory.api.InvService;
import com.neo.tcc.sample.inventory.api.InventoryService;
import com.neo.tcc.sample.inventory.bean.InvUse;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/10 15:04
 * @Description:
 */
public class InvServiceImpl implements InvService {
    private InventoryService inventoryService;

    public InvServiceImpl(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public SuccessResp preUse(InvUse inv) {
        String url = inventoryService.getHost() + "/inv/preuse";
        String res = inventoryService.post(url, inv.toJson());
        return SuccessResp.fromJson(res);
    }

    @Override
    public SuccessResp use(TransactionContext context, InvUse inv) {
        String url = inventoryService.getHost() + "/inv/use";
        String res = inventoryService.post(url, inv.toJson());
        return SuccessResp.fromJson(res);
    }

    @Override
    public SuccessResp unUse(InvUse inv) {
        String url = inventoryService.getHost() + "/inv/unuse";
        String res = inventoryService.post(url, inv.toJson());
        return SuccessResp.fromJson(res);
    }
}
