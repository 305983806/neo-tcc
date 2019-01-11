package com.neo.tcc.sample.order.common.config.inventory;

import com.lonntec.common.api.LtHttpService;
import com.neo.tcc.sample.inventory.api.InventoryService;
import com.neo.tcc.sample.inventory.api.impl.InventoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/10 15:26
 * @Description:
 */
@Configuration
@ConditionalOnClass(LtHttpService.class)
@EnableConfigurationProperties(InventoryProperties.class)
public class InventoryConfiguration {
    @Autowired
    private InventoryProperties properties;

    @Bean
    public InventoryService inventoryService() {
        InventoryService inventoryService = new InventoryServiceImpl();
        inventoryService.setHost(properties.getHost());
        return inventoryService;
    }
}
