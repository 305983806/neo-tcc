package com.neo.tcc.core.repository;

import com.neo.tcc.core.Transaction;
import com.neo.tcc.core.repository.helper.ExpandTransactionSerializer;
import com.neo.tcc.core.repository.helper.JedisCallback;
import com.neo.tcc.core.repository.helper.RedisHelper;
import com.neo.tcc.core.serializer.KryoPoolSerializer;
import com.neo.tcc.core.serializer.ObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @Auther: cp.Chen
 * @Date: 2018/12/29 15:45
 * @Description:
 */
public class RedisTransactionRepository extends CachableTransactionRepository {
    private static final Logger log = LoggerFactory.getLogger(RedisTransactionRepository.class);

    private JedisPool jedisPool;
    private String prefix = "TCC:";

    private boolean isSupportScan = true;
    private boolean isForbiddenKeys = false;

    private ObjectSerializer serializer = new KryoPoolSerializer();

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        isSupportScan = RedisHelper.isSupportScanCommand(jedisPool.getResource());
        if (!isSupportScan && isForbiddenKeys) {
            throw new RuntimeException("Redis not support 'scan' command, " +
                    "and 'keys' command is forbidden, " +
                    "try update redis version higher than 2.8.0 " +
                    "or set 'isForbiddenKeys' to false");
        }
    }

    public void setForbiddenKeys(boolean forbiddenKeys) {
        isForbiddenKeys = forbiddenKeys;
    }

    @Override
    protected int doCreate(Transaction transaction) {
        try {
            Long statusCode = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {
                @Override
                public Long doInJedis(Jedis jedis) {
                    List<byte[]> params = new ArrayList<>();
                    for (Map.Entry<byte[], byte[]> entry : ExpandTransactionSerializer.serialize(serializer, transaction).entrySet()) {
                        params.add(entry.getKey());
                        params.add(entry.getValue());
                    }

                    Object result = jedis.eval("if redis.call('exists', KEYS[1]) == 0 then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 0;".getBytes(),
                            Arrays.asList(RedisHelper.getRedisKey(prefix, transaction.getId())), params);
                    return (Long) result;
                }
            });
            return statusCode.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected int doUpdate(Transaction transaction) {
        return 0;
    }

    @Override
    protected int doDelete(Transaction transaction) {
        return 0;
    }

    @Override
    protected Transaction doGetTransaction(Transaction transaction) {
        return null;
    }

    @Override
    protected List<Transaction> doGetTimeoutTransactions(Date date) {
        return null;
    }
}
