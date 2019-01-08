package com.neo.tcc.spring.support;

import com.neo.tcc.core.support.BeanFactory;
import com.neo.tcc.core.support.FactoryBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/8 17:53
 * @Description:
 */
public class SpringBeanFactory implements BeanFactory, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public <T> T getBean(Class<T> var1) {
        return this.applicationContext.getBean(var1);
    }

    @Override
    public <T> boolean isFactoryOf(Class<T> clazz) {
        Map map = this.applicationContext.getBeansOfType(clazz);
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        FactoryBuilder.registerBeanFactory(this);
    }
}
