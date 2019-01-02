package com.neo.tcc.core.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 16:39
 * @Description:
 */
public class KryoPoolSerializer<T> implements ObjectSerializer<T> {
    private int initPoolSize = 300;

    private static KryoFactory factory = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);
            ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };
    private KryoPool pool = new KryoPool.Builder(factory).softReferences().build();

    public KryoPoolSerializer() {
        init();
    }

    public KryoPoolSerializer(int initPoolSize) {
        this.initPoolSize = initPoolSize;
    }

    private void init() {
        for (int i = 0; i < initPoolSize; i++) {
            Kryo kryo = pool.borrow();
            pool.release(kryo);
        }
    }

    @Override
    public byte[] serialize(T object) {
        return pool.run(new KryoCallback<byte[]>() {
            @Override
            public byte[] execute(Kryo kryo) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Output output = new Output(outputStream);

                kryo.writeClassAndObject(output, object);
                output.flush();

                return outputStream.toByteArray();
            }
        });
    }

    @Override
    public T deserialize(byte[] bytes) {
        return pool.run(new KryoCallback<T>() {
            @Override
            public T execute(Kryo kryo) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                Input input = new Input(inputStream);

                return (T) kryo.readClassAndObject(input);
            }
        });
    }

    @Override
    public T clone(T object) {
        return pool.run(new KryoCallback<T>() {
            @Override
            public T execute(Kryo kryo) {
                return kryo.copy(object);
            }
        });
    }
}
