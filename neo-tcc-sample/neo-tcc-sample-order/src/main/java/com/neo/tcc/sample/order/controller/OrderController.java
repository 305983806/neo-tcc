package com.neo.tcc.sample.order.controller;

import com.lonntec.common.bean.result.DefaultError;
import com.neo.tcc.sample.order.common.OrderException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/9 15:11
 * @Description:
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @GetMapping("/create")
    public String create() {
        throw new OrderException(DefaultError.SYS_BUSY);
    }
}
