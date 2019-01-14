package com.neo.tcc.sample.order.controller;

import com.lonntec.common.bean.result.DefaultError;
import com.lonntec.common.bean.result.SuccessResp;
import com.neo.tcc.sample.order.common.OrderException;
import com.neo.tcc.sample.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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
        orderService.submit();
//        throw new OrderException(DefaultError.SYS_HTTP_NO_RESPONSE);
        return SuccessResp.build();
    }

    @GetMapping("/pay")
    public SuccessResp pay() {
        //TODO 调用第三方支付接口
        //TODO 修改订单状态为已支付
        //TODO 扣减库存
        return SuccessResp.build();
    }
}
