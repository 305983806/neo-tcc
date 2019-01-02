package com.neo.tcc.core.support;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/2 22:14
 * @Description: Bean 工厂接口，用于和 Spring Bean 容器解耦
 */
public interface BeanFactory {
    /**
     * 获得 Bean
     *
     * @param var1 类
     * @param <T> 泛型
     * @return Bean
     */
    <T> T getBean(Class<T> var1);

    /**
     * 判断工厂生产的类是否为指定类
     *
     * @param clazz 指定类
     * @param <T> 泛型
     * @return 是/否
     */
    <T> boolean isFactoryOf(Class<T> clazz);
}
