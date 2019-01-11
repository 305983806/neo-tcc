package com.neo.tcc.sample.inventory.service;

import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.sample.inventory.bean.InvUse;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/11 17:07
 * @Description:
 */
public interface InvService {
    void use(TransactionContext transactionContext, InvUse invUse);
}
