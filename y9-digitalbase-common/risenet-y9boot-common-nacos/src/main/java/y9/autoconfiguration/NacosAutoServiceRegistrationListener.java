package y9.autoconfiguration;

//import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;

import lombok.RequiredArgsConstructor;

/**
 * nacos 自动服务注册监听器
 *
 * @author shidaobang
 * @date 2024/02/06
 */
@RequiredArgsConstructor
public class NacosAutoServiceRegistrationListener implements ApplicationListener<ApplicationReadyEvent> {

    private final NacosAutoServiceRegistration registration;
    private final ServerProperties serverProperties;

    @Override
    @SuppressWarnings("deprecation")
    public void onApplicationEvent(ApplicationReadyEvent event) {
        registration.setPort(serverProperties.getPort());
        registration.start();
    }
}
