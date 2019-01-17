package com.neo.tcc.sample.inventory.api;

import com.lonntec.common.bean.result.SuccessResp;
import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.sample.inventory.bean.InvUse;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/10 14:39
 * @Description:
 */
public interface InvService {
    /**
     * 预占库存
     * @param inv
     */
    SuccessResp preUse(InvUse inv);

    /***
     * 扣减库存
     * @param inv
     */
    SuccessResp use(TransactionContext transactionContext, InvUse inv);

    /**
     * 取消预占库存
     * @param inv
     */
    SuccessResp unUse(InvUse inv);
}
