package com.neo.tcc.core.repository.helper;

import com.alibaba.fastjson.JSON;
import com.neo.tcc.core.SystemException;
import com.neo.tcc.core.Transaction;
import com.neo.tcc.core.api.TransactionStatus;
import com.neo.tcc.core.serializer.ObjectSerializer;
import com.neo.tcc.core.utils.ByteUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 15:59
 * @Description:
 */
public class ExpandTransactionSerializer {
    public static Map<byte[], byte[]> serialize(ObjectSerializer serializer, Transaction transaction) {

        Map<byte[], byte[]> map = new HashMap<>();

        map.put("GLOBAL_TX_ID".getBytes(), transaction.getId().getGlobalTransactionId());
        map.put("BRANCH_QUALIFIER".getBytes(), transaction.getId().getBranchQualifier());
        map.put("STATUS".getBytes(), ByteUtils.intToBytes(transaction.getStatus().getId()));
        map.put("TRANSACTION_TYPE".getBytes(), ByteUtils.intToBytes(transaction.getTransactionType().getId()));
        map.put("RETRY_TIME".getBytes(), ByteUtils.intToBytes(transaction.getRetryTimes()));
        map.put("CREATE_TIME".getBytes(), DateFormatUtils.format(transaction.getCreateTime(), "yyyy-MM-dd HH:mm:ss").getBytes());
        map.put("LAST_UPDATE_TIME".getBytes(), DateFormatUtils.format(transaction.getLastUpdateTime(), "yyyy-MM-dd HH:mm:ss").getBytes());
        map.put("VERSION".getBytes(), ByteUtils.longToBytes(transaction.getVersion()));
        map.put("CONTENT".getBytes(), serializer.serialize(transaction));
        map.put("CONTENT_VIEW".getBytes(), JSON.toJSONString(transaction).getBytes());
        return map;
    }

    public static Transaction deserialize(ObjectSerializer serializer, Map<byte[], byte[]> map1) {

        Map<String, byte[]> propertyMap = new HashMap<>();

        for (Map.Entry<byte[], byte[]> entry : map1.entrySet()) {
            propertyMap.put(new String(entry.getKey()), entry.getValue());
        }

        byte[] content = propertyMap.get("CONTENT");
        Transaction transaction = (Transaction) serializer.deserialize(content);
        transaction.changeStatus(TransactionStatus.valueOf(ByteUtils.bytesToInt(propertyMap.get("STATUS"))));
        transaction.resetRetryTimes(ByteUtils.bytesToInt(propertyMap.get("RETRY_TIME")));

        try {
            transaction.setLastUpdateTime(DateUtils.parseDate(new String(propertyMap.get("LAST_UPDATE_TIME")), "yyyy-MM-dd HH:mm:ss"));
        } catch (ParseException e) {
            throw new SystemException(e);
        }

        transaction.setVersion(ByteUtils.bytesToLong(propertyMap.get("VERSION")));
        return transaction;
    }
}
