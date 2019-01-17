package com.neo.tcc.sample.inventory.service.impl;

import com.lonntec.common.bean.result.DefaultError;
import com.neo.tcc.api.Compensable;
import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.core.context.MethodTransactionContextEditor;
import com.neo.tcc.sample.inventory.bean.Inv;
import com.neo.tcc.sample.inventory.bean.InvUse;
import com.neo.tcc.sample.inventory.common.config.InventoryException;
import com.neo.tcc.sample.inventory.service.InvService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/11 17:13
 * @Description:
 */
@Service
public class InvServiceImpl implements InvService {
    private static final Logger log = LoggerFactory.getLogger(InvServiceImpl.class);

    @Override
    @Compensable(confirmMethod = "doUse", cancelMethod = "unUse", transactionContextEditor =
            MethodTransactionContextEditor.class)
    @Transactional
    public void use(TransactionContext transactionContext, Inv inv) {
        Random random = new Random();
        int flag = (random.nextInt(1) + 1);
        log.info("random is : {}", flag);
        if (flag ==1) {
            throw new InventoryException(DefaultError.SYS_BUSY);
        }
        log.info("try 占用库存：生成库存占用记录，并调整状态为\"预占\"");
    }

    public void doUse() {
        log.info("commit 占用库存：生成库存占用记录，并调整状态为\"占用\"");
    }

    public void unUse() {
        log.info("cancel 占用库存：生成库存占用记录，并调整状态为\"取消占用\"");
    }
}
