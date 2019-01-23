package com.neo.tcc.sample.member;

import com.neo.rpc.server.RpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/22 17:42
 * @Description:
 */
@SpringBootApplication(scanBasePackages = {
        "com.neo.tcc.sample.*",
        "com.neo.rpc"
})
public class Server {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Server.class);
        ctx.getBean(RpcServer.class).start();
    }
}
