package com.neo.tcc.sample.order.controller;

import com.lonntec.common.bean.result.SuccessResp;
import com.neo.tcc.sample.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/9 15:11
 * @Description:
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/create")
    public SuccessResp create() {
        //TODO 预占库
        orderService.tryOrder();
        return SuccessResp.build();
    }
}
