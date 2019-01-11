package com.neo.tcc.sample.order.service.impl;

import com.neo.tcc.api.Compensable;
import com.neo.tcc.sample.inventory.api.InventoryService;
import com.neo.tcc.sample.inventory.bean.InvUse;
import com.neo.tcc.sample.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/10 19:00
 * @Description:
 */
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private InventoryServiceProxy inventoryServiceProxy;

    @Override
    @Compensable(confirmMethod = "doSubmit", cancelMethod = "cancelSubmit")
    @Transactional
    public void submit() {
        log.info("try 提交订单：保存订单，并设置订单状态为\"草稿\"");
        inventoryServiceProxy.use(null, new InvUse());
        log.info("try 提交订单：inventoryServiceProxy->use 调用占用库存结束");
    }

    /**
     * 确认提交订单
     */
    public void doSubmit() {
        log.info("commit 提交订单: 修改订单状态为\"提交\"");
    }

    /**
     * 取消提交订单
     */
    public void cancelSubmit () {
        log.info("cancel 提交订单: 修改订单状态为\"关闭\"");
    }

    @Override
    public void pay() {

    }
}