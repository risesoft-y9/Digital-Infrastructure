package net.risesoft.log.service;

import org.springframework.scheduling.annotation.Async;

import net.risesoft.model.log.AccessLog;

/**
 * 访问日志推送
 *
 * @author shidaobang
 * @date 2024/11/14
 * @since 9.6.8
 */
public interface AccessLogPusher {

    @Async("y9ThreadPoolTaskExecutor")
    void push(AccessLog accessLog);

}
