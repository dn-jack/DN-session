package com.dongnao.jack.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {
    
    private static ApplicationContext applicationContext;
    
    public void setApplicationContext(ApplicationContext args)
            throws BeansException {
        applicationContext = args;
    }
    
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
}
