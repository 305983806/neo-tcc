package com.neo.tcc.sample.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/9 14:17
 * @Description:
 */
@SpringBootApplication(scanBasePackages = {
        "com.neo.tcc",
        "com.neo.rpc"
})
public class Server {
    public static void main(String[] args) {
        SpringApplication.run(Server.class);
    }
}
