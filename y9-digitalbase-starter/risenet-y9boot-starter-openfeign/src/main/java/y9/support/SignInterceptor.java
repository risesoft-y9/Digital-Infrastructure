package y9.support;

import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;

import net.risesoft.ApiSignUtil;
import net.risesoft.y9.configuration.feature.openfeign.Y9SignProperties;

import cn.hutool.core.util.URLUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 签名拦截器
 *
 * @author shidaobang
 * @date 2024/11/29
 */
@RequiredArgsConstructor
public class SignInterceptor implements RequestInterceptor {

    private final static String APP_ID_HEADER = "x-app-id";
    private final static String TIMESTAMP_HEADER = "x-timestamp";
    private final static String SIGNATURE_HEADER = "x-signature";

    private final Y9SignProperties y9SignProperties;

    @Override
    public void apply(RequestTemplate template) {

        String appId = y9SignProperties.getAppId();
        String appSecret = y9SignProperties.getAppSecret();
        String path = URLUtil.getPath(template.feignTarget().url() + template.path());
        String queryString = URLUtil.buildQuery(template.queries(), StandardCharsets.UTF_8);
        String body = template.body() == null ? "" : new String(template.body(), StandardCharsets.UTF_8);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = ApiSignUtil.sign(appId, appSecret, path, queryString, body, timestamp);

        template.header(APP_ID_HEADER, appId);
        template.header(TIMESTAMP_HEADER, timestamp);
        template.header(SIGNATURE_HEADER, signature);
    }

}
