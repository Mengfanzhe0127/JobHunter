package com.group.configurator;

import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.ApplicationContext;

/**
 * @Author: mfz
 * @Date: 2024/05/05/0:02
 * @Description:
 */
public class CustomSpringConfigurator extends ServerEndpointConfig.Configurator {

    private static volatile ApplicationContext context;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        CustomSpringConfigurator.context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return context.getBean(endpointClass);
    }
}

