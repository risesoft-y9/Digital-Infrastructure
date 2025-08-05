package net.risesoft.log.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.service.AccessLogReporter;
import net.risesoft.model.log.AccessLog;
import net.risesoft.y9.json.Y9JsonUtil;

@Slf4j
@RequiredArgsConstructor
public class AccessLogConsoleReporter implements AccessLogReporter {

    @Override
    public void report(AccessLog accessLog) {
        LOGGER.debug(Y9JsonUtil.writeValueAsString(accessLog));
    }

}
