package com.neo.tcc.sample.inventory.service.impl;

import com.neo.tcc.api.Compensable;
import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.core.context.MethodTransactionContextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/24 17:08
 * @Description:
 */
@Component
public class InventoryTCCService {
    private static final Logger log = LoggerFactory.getLogger(InventoryTCCService.class);

    @Compensable(confirmMethod = "confirmUse", cancelMethod = "cancelUse", transactionContextEditor = MethodTransactionContextEditor.class)
    public void preUse(TransactionContext transactionContext) {
        log.debug("预占库存：try，30分钟内未确认将被释放。");
//        throw new RuntimeException("测试TCC异常：inventory 预占库存出现异常。");
    }

    public void confirmUse(TransactionContext transactionContext) {
        log.debug("确认占用：confirm。");
    }

    public void cancelUse(TransactionContext transactionContext) {
        log.debug("取消占用：cancel。");
    }
}
