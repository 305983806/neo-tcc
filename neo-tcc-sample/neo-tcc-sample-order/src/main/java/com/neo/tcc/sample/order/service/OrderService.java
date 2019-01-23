package com.neo.tcc.sample.order.service;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/22 18:03
 * @Description:
 */
public interface OrderService {
    void tryOrder();

    void commitOrder();

    void cancelOrder();
}
