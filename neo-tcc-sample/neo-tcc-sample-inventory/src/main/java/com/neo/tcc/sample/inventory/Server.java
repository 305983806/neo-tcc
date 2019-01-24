package com.neo.tcc.sample.inventory;

import com.neo.rpc.server.RpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/9 14:44
 * @Description:
 */
@SpringBootApplication(scanBasePackages = {
        "com.neo.tcc",
        "com.neo.rpc"
})
public class Server {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Server.class);
        ctx.getBean(RpcServer.class).start();
    }
}
