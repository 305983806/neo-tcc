package com.neo.tcc.sample.inventory.service.impl;

import com.neo.rpc.server.RpcService;
import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.sample.inventory.api.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    @Override
    public void preUse(TransactionContext transactionContext) {
        log.debug("预占库存：成功，30分钟内未确认将被释放。");
    }

    @Override
    public void confirmUse(TransactionContext transactionContext) {
        log.debug("确认占用：成功。");
    }

    @Override
    public void cancelUse(TransactionContext transactionContext) {
        log.debug("取消占用：成功");
    }
}
