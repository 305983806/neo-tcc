package com.neo.tcc.core.repository.helper;

import redis.clients.jedis.Jedis;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 15:44
 * @Description:
 */
public interface JedisCallback<T> {
    public T doInJedis(Jedis jedis);
}
