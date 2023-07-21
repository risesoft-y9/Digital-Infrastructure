package y9.apisix;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import lombok.RequiredArgsConstructor;

import y9.apisix.register.Y9RegisterByApisixRestApi;

@RequiredArgsConstructor
public class OnY9ApisixRegisterApplicationReady implements ApplicationListener<ApplicationReadyEvent> {

    private final Y9RegisterByApisixRestApi y9RegisterByApisixRestApi;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        y9RegisterByApisixRestApi.registerApiToApisix();
    }

}
