package com.neo.tcc.core.repository;

import com.neo.tcc.core.Transaction;
import com.neo.tcc.core.repository.helper.ExpandTransactionSerializer;
import com.neo.tcc.core.repository.helper.JedisCallback;
import com.neo.tcc.core.repository.helper.RedisHelper;
import com.neo.tcc.core.serializer.KryoPoolSerializer;
import com.neo.tcc.core.serializer.ObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import javax.transaction.xa.Xid;
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
    private int fetchKeySize = 1000;

    private boolean isSupportScan = true;
    private boolean isForbiddenKeys = false;

    private ObjectSerializer serializer = new KryoPoolSerializer();

    public int getFetchKeySize() {
        return fetchKeySize;
    }

    public void setFetchKeySize(int fetchKeySize) {
        this.fetchKeySize = fetchKeySize;
    }

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

    public void setSupportScan(boolean supportScan) {
        isSupportScan = supportScan;
    }

    public void setForbiddenKeys(boolean forbiddenKeys) {
        isForbiddenKeys = forbiddenKeys;
    }

    @Override
    protected int doCreate(final Transaction transaction) {
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
    protected int doUpdate(final Transaction transaction) {
        try {
            Long statusCode = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {
                @Override
                public Long doInJedis(Jedis jedis) {
                    transaction.updateTime();
                    transaction.updateVersion();

                    List<byte[]> params = new ArrayList<>();
                    for (Map.Entry<byte[], byte[]> entry : ExpandTransactionSerializer.serialize(serializer, transaction).entrySet()) {
                        params.add(entry.getKey());
                        params.add(entry.getValue());
                    }
                    Object result = jedis.eval(
                            String.format("if redis.call('hget',KEYS[1],'VERSION') = '%s' then redis.call('hmset', KEYS[1], unpack(ARGV)); return 1; end; return 0;", transaction.getVersion() - 1).getBytes(),
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
    protected int doDelete(Transaction transaction) {
        try {
            Long result = RedisHelper.execute(jedisPool, new JedisCallback<Long>() {
                @Override
                public Long doInJedis(Jedis jedis) {
                    return jedis.del(RedisHelper.getRedisKey(prefix, transaction.getId()));
                }
            });
            return result.intValue();
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected Transaction doGetTransaction(Xid id) {
        try {
            Long startTime = System.currentTimeMillis();
            Map<byte[], byte[]> content = RedisHelper.execute(jedisPool, new JedisCallback<Map<byte[], byte[]>>() {
                @Override
                public Map<byte[], byte[]> doInJedis(Jedis jedis) {
                    return jedis.hgetAll(RedisHelper.getRedisKey(prefix, id));
                }
            });
            return null;
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }

    @Override
    protected List<Transaction> doGetTimeoutTransactions(Date date) {
        List<Transaction> transactions = doFindAll();
        List<Transaction> timeoutTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getLastUpdateTime().compareTo(date) < 0) {
                timeoutTransactions.add(transaction);
            }
        }
        return timeoutTransactions;
    }

    protected List<Transaction> doFindAll() {
        try {
            final Set<byte[]> keys = RedisHelper.execute(jedisPool, new JedisCallback<Set<byte[]>>() {
                @Override
                public Set<byte[]> doInJedis(Jedis jedis) {
                    if (isSupportScan) {
                        List<String> keys = new ArrayList<>();
                        String cursor = RedisHelper.SCAN_INIT_CURSOR;
                        ScanParams scanParams = RedisHelper.buildDefaultScanParams(prefix + "*", fetchKeySize);
                        do {
                            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                            keys.addAll(scanResult.getResult());
                            cursor = scanResult.getStringCursor();
                        } while (!cursor.equals(RedisHelper.SCAN_INIT_CURSOR));

                        Set<byte[]> keySet = new HashSet<>();
                        for (String key : keys) {
                            keySet.add(key.getBytes());
                        }
                        log.info("find all key by scan command with pattern:{} keysSet.size()={}", prefix + "*", keySet.size());
                        return keySet;
                    } else {
                        return jedis.keys((prefix + "*").getBytes());
                    }
                }
            });

            return RedisHelper.execute(jedisPool, new JedisCallback<List<Transaction>>() {
                @Override
                public List<Transaction> doInJedis(Jedis jedis) {
                    Pipeline pipeline = jedis.pipelined();
                    for (final byte[] key : keys) {
                        pipeline.hgetAll(key);
                    }
                    List<Object> result = pipeline.syncAndReturnAll();
                    List<Transaction> list = new ArrayList<>();
                    for (Object data : result) {
                        if (data != null && ((Map<byte[], byte[]>) data).size() > 0) {
                            list.add(ExpandTransactionSerializer.deserialize(serializer, (Map<byte[], byte[]>) data));
                        }
                    }
                    return list;
                }
            });
        } catch (Exception e) {
            throw new TransactionIOException(e);
        }
    }
}
