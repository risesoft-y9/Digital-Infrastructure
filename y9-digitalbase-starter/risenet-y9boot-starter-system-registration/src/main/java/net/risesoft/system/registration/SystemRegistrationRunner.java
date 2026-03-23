package net.risesoft.system.registration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.model.platform.SystemJsonModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.configuration.feature.systemregistration.Y9SystemRegistrationProperties;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 系统在数字底座中注册执行器
 *
 * @author shidaobang
 * @since 9.6.10
 */
@RequiredArgsConstructor
@Slf4j
public class SystemRegistrationRunner implements ApplicationListener<ApplicationReadyEvent> {

    private final Y9SystemRegistrationProperties properties;
    private final ResourceLoader resourceLoader;
    private final SystemApi systemApi;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            String json = this.load(properties.getLocation());
            if (StringUtils.isBlank(json)) {
                LOGGER.info("系统 JSON 内容为空，跳过系统在数字底座中注册。");
                return;
            }

            SystemJsonModel systemJsonModel = Y9JsonUtil.readValue(json, SystemJsonModel.class);
            if (systemJsonModel == null) {
                LOGGER.warn("系统 JSON 内容不合法，跳过系统在数字底座中注册。");
                return;
            }

            Y9Result<Object> result = systemApi.register(systemJsonModel);
            if (!result.isSuccess()) {
                LOGGER.warn("系统在数字底座中注册失败，跳过本次系统在数字底座中注册。msg={}", result.getMsg());
                return;
            }

            LOGGER.info("系统启动在数字底座中注册完成");
        } catch (Exception e) {
            LOGGER.warn("系统在数字底座中注册出现异常，已跳过。", e);
        }
    }

    private String load(String location) {
        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            LOGGER.info("未找到系统 JSON 资源，跳过系统在数字底座中注册。location={}", location);
            return null;
        }
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return IOUtils.toString(reader);
        } catch (IOException e) {
            LOGGER.warn("读取系统 JSON 失败，跳过系统在数字底座中注册。location={}", location, e);
            return null;
        }
    }
}
