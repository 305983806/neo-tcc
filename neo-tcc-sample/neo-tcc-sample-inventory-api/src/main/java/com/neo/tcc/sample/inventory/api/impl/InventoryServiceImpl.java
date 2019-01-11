package com.neo.tcc.sample.inventory.api.impl;

import com.lonntec.common.api.impl.LtHttpServiceJoddHttpImpl;
import com.neo.tcc.sample.inventory.api.InvService;
import com.neo.tcc.sample.inventory.api.InventoryService;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/10 15:05
 * @Description:
 */
public class InventoryServiceImpl extends LtHttpServiceJoddHttpImpl implements InventoryService {
    private InvService invService = new InvServiceImpl(this);

    @Override
    public InvService getInvService() {
        return this.invService;
    }
}
