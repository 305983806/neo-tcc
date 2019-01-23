package com.neo.tcc.sample.member.api;

import com.neo.tcc.api.TransactionContext;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/22 16:38
 * @Description:
 */
public interface BonuspointService {

    void preUse(TransactionContext transactionContext);

    void confirmUse(TransactionContext transactionContext);

    void cancelUse(TransactionContext transactionContext);
}
