package net.risesoft;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OnApplicationContextInitialized implements ApplicationListener<ApplicationContextInitializedEvent> {

    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
        LOGGER.info("log ApplicationContextInitialized...");
        ConfigurableListableBeanFactory beanFactory = event.getApplicationContext().getBeanFactory();
        if (beanFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory bf = (DefaultListableBeanFactory)beanFactory;
            bf.setAllowRawInjectionDespiteWrapping(true);
        }
    }
}
