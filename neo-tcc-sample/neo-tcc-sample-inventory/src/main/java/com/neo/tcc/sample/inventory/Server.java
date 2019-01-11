package com.neo.tcc.sample.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/9 14:44
 * @Description:
 */
@SpringBootApplication(scanBasePackages = {
        "com.lonntec.springSupport.handler",
        "com.lonntec.springSupport.valid",
        "com.neo.tcc.*"
})
public class Server {
    public static void main(String[] args) {
        SpringApplication.run(Server.class);
    }
}