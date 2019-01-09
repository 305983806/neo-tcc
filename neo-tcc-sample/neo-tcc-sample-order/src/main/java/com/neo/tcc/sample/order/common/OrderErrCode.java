package com.neo.tcc.sample.order.common;

import com.lonntec.common.bean.result.Error;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/9 15:22
 * @Description:
 */
public class OrderErrCode implements Error {
    ;

    private String code;
    private String message;

    public OrderErrCode(String code) {
        this.code = code;
    }

    public OrderErrCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
