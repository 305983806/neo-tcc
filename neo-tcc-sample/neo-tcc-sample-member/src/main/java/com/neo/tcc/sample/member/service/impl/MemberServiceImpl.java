package com.neo.tcc.sample.member.service.impl;

import com.neo.rpc.server.RpcService;
import com.neo.tcc.api.TransactionContext;
import com.neo.tcc.sample.member.api.BonuspointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/22 17:46
 * @Description:
 */
@RpcService(BonuspointService.class)
public class MemberServiceImpl implements BonuspointService {
    private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Override
    public void preUse(TransactionContext transactionContext) {
        log.debug("预扣积分：成功，30分钟内未确认将被释放。");
    }

    @Override
    public void confirmUse(TransactionContext transactionContext) {
        log.debug("确认扣减积分：成功。");
    }

    @Override
    public void cancelUse(TransactionContext transactionContext) {
        log.debug("取消积分扣减：成功");
    }
}
