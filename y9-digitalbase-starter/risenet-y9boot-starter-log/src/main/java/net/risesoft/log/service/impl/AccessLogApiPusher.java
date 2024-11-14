package net.risesoft.log.service.impl;

import java.util.List;

import org.apache.commons.httpclient.NameValuePair;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.service.AccessLogPusher;
import net.risesoft.model.log.AccessLog;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.util.RemoteCallUtil;

/**
 * 访问日志 API 推送
 *
 * @author shidaobang
 * @date 2024/11/14
 * @since 9.6.8
 */
@Slf4j
@RequiredArgsConstructor
public class AccessLogApiPusher implements AccessLogPusher {

    private final Y9Properties y9Properties;

    @Override
    public void push(final AccessLog log) {
        String logBaseUrl = y9Properties.getCommon().getLogBaseUrl();
        String url = logBaseUrl + "/services/rest/v1/accessLog/asyncSaveLog";
        List<NameValuePair> requestBody = RemoteCallUtil.objectToNameValuePairList(log);
        RemoteCallUtil.post(url, null, requestBody, Object.class);
    }
}
