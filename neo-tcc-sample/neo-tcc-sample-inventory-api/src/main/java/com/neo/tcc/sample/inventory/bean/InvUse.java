package com.neo.tcc.sample.inventory.bean;

import com.alibaba.fastjson.JSON;
import com.neo.tcc.api.TransactionContext;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/10 14:42
 * @Description:
 */
public class InvUse {
    @NotEmpty
    private String number;

    @Size(min = 1)
    @Valid
    private List<InvUseItem> items = new ArrayList<>();

    private TransactionContext transactionContext;

    public static InvUse fromJson(String json) {
        return JSON.parseObject(json, InvUse.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<InvUseItem> getItems() {
        return items;
    }

    public void setItems(List<InvUseItem> items) {
        this.items = items;
    }

    public TransactionContext getTransactionContext() {
        return transactionContext;
    }

    public void setTransactionContext(TransactionContext transactionContext) {
        this.transactionContext = transactionContext;
    }
}
