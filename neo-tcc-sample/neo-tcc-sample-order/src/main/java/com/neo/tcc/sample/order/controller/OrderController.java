package com.neo.tcc.sample.order.controller;

import com.lonntec.common.bean.result.DefaultError;
import com.lonntec.common.bean.result.SuccessResp;
import com.neo.tcc.sample.order.common.OrderException;
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

    @GetMapping("/create")
    public SuccessResp create(@CookieValue String ctego_session) {
        //TODO 预占库

        throw new OrderException(DefaultError.SYS_HTTP_NO_RESPONSE);
    }

    @GetMapping("/pay")
    public SuccessResp pay() {
        //TODO 调用第三方支付接口
        //TODO 修改订单状态为已支付
        //TODO 扣减库存
        return SuccessResp.build();
    }
}
