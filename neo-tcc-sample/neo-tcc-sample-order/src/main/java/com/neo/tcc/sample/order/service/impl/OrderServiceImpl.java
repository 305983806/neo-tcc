package com.neo.tcc.sample.order.service.impl;

import com.neo.rpc.client.RpcClient;
import com.neo.tcc.api.Compensable;
import com.neo.tcc.sample.inventory.api.InventoryService;
import com.neo.tcc.sample.member.api.BonuspointService;
import com.neo.tcc.sample.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/22 18:11
 * @Description:
 */
@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private TradeServiceProxy tradeServiceProxy;

    @Override
    @Compensable(confirmMethod = "commitOrder", cancelMethod = "cancelOrder", asyncConfirm = true)
    public void tryOrder() {
        log.debug("保存订单成功：[草稿]");

        // 占用库存
        tradeServiceProxy.useInv(null);

        // 扣减积分
        tradeServiceProxy.useBonuspoint(null);
    }

    @Override
    public void commitOrder() {
        log.debug("保存订单成功：[提交]");
    }

    @Override
    public void cancelOrder() {
        log.debug("保存订单失败：[关闭]");
    }
}
