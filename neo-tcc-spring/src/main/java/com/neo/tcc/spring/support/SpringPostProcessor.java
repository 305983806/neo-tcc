package com.neo.tcc.spring.support;

import com.neo.tcc.core.support.BeanFactory;
import com.neo.tcc.core.support.FactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @Auther: cp.Chen
 * @Date: 2019/1/8 18:05
 * @Description:
 */
public class SpringPostProcessor implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();

        if (applicationContext.getParent() == null) {
            FactoryBuilder.registerBeanFactory(applicationContext.getBean(BeanFactory.class));
        }
    }
}
