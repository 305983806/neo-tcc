package com.neo.tcc.core.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 22:14
 * @Description:
 */
public class FactoryBuilder {
    /**
     * Bean 工厂集合
     */
    private static List<BeanFactory> beanFactories = new ArrayList<>();
    /**
     * 类 与 Bean 工厂的映射
     */
    private static ConcurrentHashMap<Class, SingletonFactory> classFactoryMap = new ConcurrentHashMap<>();

    /**
     * 将 Bean 工厂注册到当前 Builder
     *
     * @param beanFactory Bean工厂
     */
    public static void registerBeanFactory(BeanFactory beanFactory) {
        beanFactories.add(beanFactory);
    }

    /**
     * 获得指定类单例工厂
     *
     * @param clazz 指定类
     * @param <T> 泛型
     * @return 单例工厂
     */
    public static <T> SingletonFactory<T> factoryOf(Class<T> clazz) {
        if (!classFactoryMap.containsKey(clazz)) {
            // 优先从 Bean 工厂集合获取
            for (BeanFactory beanFactory : beanFactories) {
                if (beanFactory.isFactoryOf(clazz)) {
                    classFactoryMap.putIfAbsent(clazz, new SingletonFactory<T>(clazz, beanFactory.getBean(clazz)));
                }
            }
            // 查找不到，再创建 SingletonFactory
            if (!classFactoryMap.containsKey(clazz)) {
                classFactoryMap.putIfAbsent(clazz, new SingletonFactory<T>(clazz));
            }
        }
        return classFactoryMap.get(clazz);
    }

    /**
     * 单例工厂
     *
     * @param <T> 泛型
     */
    public static class SingletonFactory<T> {
        /**
         * 单例
         */
        private volatile T instance = null;
        /**
         * 类名
         */
        private String className;

        public SingletonFactory(Class<T> clazz) {
            this.className = clazz.getName();
        }

        public SingletonFactory(Class<T> clazz, T instance) {
            this.className = clazz.getName();
            this.instance = instance;
        }

        /**
         * 获得单例
         *
         * @return 单例
         */
        public T getInstance() {
            if (instance == null) {
                // 不存在时，创建单例
                synchronized (SingletonFactory.class) {
                    if (instance == null) {
                        try {
                            ClassLoader loader = Thread.currentThread().getContextClassLoader();
                            Class<?> clazz = loader.loadClass(className);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException("Failed to create an instance of " + className, e);
                        }
                    }
                }
            }
            return instance;
        }

        @Override
        public int hashCode() {
            return className.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            SingletonFactory that = (SingletonFactory) obj;
            if (!className.equals(that.className)) return true;

            return true;
        }
    }
}
