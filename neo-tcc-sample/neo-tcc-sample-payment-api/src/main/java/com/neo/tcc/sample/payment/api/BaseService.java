package com.neo.tcc.sample.payment.api;

import com.lonntec.common.api.LtHttpService;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/11 09:23
 * @Description:
 */
public interface BaseService extends LtHttpService {

    @Override
    void setHost(String s);

    @Override
    String getHost();

    PaymentService getPaymentService();
}
