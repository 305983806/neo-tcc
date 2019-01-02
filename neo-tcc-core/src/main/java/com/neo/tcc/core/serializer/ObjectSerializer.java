package com.neo.tcc.core.serializer;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 16:00
 * @Description:
 */
public interface ObjectSerializer<T> {
    /**
     * Serialize the given object to binary data.
     *
     * @param t object to serialize
     * @return the equivalent binary data
     */
    byte[] serialize(T t);

    /**
     * Deserialize the given object to binary data.
     *
     * @param bytes object binary representation
     * @return the equivalent object instance
     */
    T deserialize(byte[] bytes);

    T clone(T object);
}
