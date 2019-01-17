package com.neo.tcc.sample.inventory.common.config;

import com.lonntec.common.bean.result.Error;
import com.lonntec.common.lang.LtBusinessException;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/16 09:33
 * @Description:
 */
public class InventoryException extends LtBusinessException {
    public InventoryException(Error error) {
        super(error);
    }

    public InventoryException(Error error, Throwable cause) {
        super(error, cause);
    }

    public InventoryException(String message, Throwable cause, Error error) {
        super(message, cause, error);
    }
}
