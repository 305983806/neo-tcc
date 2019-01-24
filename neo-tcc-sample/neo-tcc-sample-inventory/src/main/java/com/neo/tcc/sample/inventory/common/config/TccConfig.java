package com.neo.tcc.sample.inventory.common.config;


import com.neo.tcc.core.repository.RedisTransactionRepository;
import com.neo.tcc.core.serializer.KryoPoolSerializer;
import com.neo.tcc.spring.recover.DefaultRecoverConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/11 17:49
 * @Description:
 */
@Configuration
public class TccConfig {

    @Bean
    public DefaultRecoverConfig recoverConfig() {
        DefaultRecoverConfig recoverConfig = new DefaultRecoverConfig();
        recoverConfig.setMaxRetryCount(2);
        recoverConfig.setRecoverDuration(5);
        recoverConfig.setCronExpression("0/5 * * * * ?");
        return recoverConfig;
    }

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(50);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(1000*100);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        JedisPool pool = new JedisPool(config, "192.168.171.17", 6379, 6000, "lonntecCom88");
        return pool;
    }

    @Bean
    public RedisTransactionRepository transactionRepository(JedisPool jedisPool) {
        RedisTransactionRepository repository = new RedisTransactionRepository();
        repository.setJedisPool(jedisPool);
        return repository;
    }

    @Bean
    public KryoPoolSerializer objectSerializer() {
        return new KryoPoolSerializer();
    }
}
