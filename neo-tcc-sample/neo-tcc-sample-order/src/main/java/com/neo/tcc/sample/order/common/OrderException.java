package com.neo.tcc.sample.order.common;

import com.lonntec.common.bean.result.Error;
import com.lonntec.common.lang.LtBusinessException;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/9 15:21
 * @Description:
 */
public class OrderException extends LtBusinessException {
    public OrderException(Error error) {
        super(error);
    }

    public OrderException(Error error, Throwable cause) {
        super(error, cause);
    }

    public OrderException(String message, Throwable cause, Error error) {
        super(message, cause, error);
    }
}
