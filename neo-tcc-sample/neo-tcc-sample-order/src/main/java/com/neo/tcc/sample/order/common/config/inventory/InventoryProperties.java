package com.neo.tcc.sample.order.common.config.inventory;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/10 15:25
 * @Description:
 */
@ConfigurationProperties(prefix = "sample.inventory")
public class InventoryProperties {

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
