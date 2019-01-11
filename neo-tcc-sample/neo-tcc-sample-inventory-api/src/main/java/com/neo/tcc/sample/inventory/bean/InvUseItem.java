package com.neo.tcc.sample.inventory.bean;

import com.alibaba.fastjson.JSON;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/10 14:44
 * @Description:
 */
public class InvUseItem {
    @NotEmpty
    private String articleNumber;

    @Min(1)
    private long qty;

    public static InvUseItem fromJson(String json) {
        return JSON.parseObject(json, InvUseItem.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public long getQty() {
        return qty;
    }

    public void setQty(long qty) {
        this.qty = qty;
    }
}
