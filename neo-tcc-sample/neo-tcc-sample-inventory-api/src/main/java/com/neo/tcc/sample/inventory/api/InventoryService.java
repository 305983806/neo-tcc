package com.neo.tcc.sample.inventory.api;

import com.lonntec.common.api.LtHttpService;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/10 14:37
 * @Description:
 */
public interface InventoryService extends LtHttpService {
    @Override
    void setHost(String s);

    @Override
    String getHost();

    InvService getInvService();
}
