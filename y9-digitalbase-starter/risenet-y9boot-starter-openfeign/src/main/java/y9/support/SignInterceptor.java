package y9.support;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import lombok.RequiredArgsConstructor;

import net.risesoft.ApiSignUtil;
import net.risesoft.y9.configuration.feature.openfeign.Y9SignProperties;

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
        URI requestUri = resolveRequestUri(template);
        String rawPath = requestUri.getRawPath();
        String path = rawPath.isEmpty() ? "/" : rawPath;
        String body = template.body() == null ? "" : new String(template.body(), StandardCharsets.UTF_8);
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String signature = ApiSignUtil.sign(appId, appSecret, path, requestUri.getRawQuery(), body, timestamp);

        template.header(APP_ID_HEADER, appId);
        template.header(TIMESTAMP_HEADER, timestamp);
        template.header(SIGNATURE_HEADER, signature);
    }

    private static URI resolveRequestUri(RequestTemplate template) {
        URI uri = URI.create(template.url());
        if (uri.isAbsolute()) {
            return uri;
        }

        // 在模板副本上按 Feign 的规则合并目标地址，保留路径前缀和查询参数。
        RequestTemplate targetedTemplate = RequestTemplate.from(template).target(template.feignTarget().url());
        return URI.create(targetedTemplate.url());
    }

}
