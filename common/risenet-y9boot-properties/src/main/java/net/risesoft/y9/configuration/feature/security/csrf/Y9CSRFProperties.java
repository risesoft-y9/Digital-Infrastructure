package net.risesoft.y9.configuration.feature.security.csrf;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * csrf 配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9CSRFProperties {

    /**
     * 是否开启csrf过滤 默认开启
     */
    private boolean enabled = true;

    /**
     * 允许的请求 referer 填入域名即可
     */
    private List<String> acceptedReferer = Arrays.asList("localhost", "127.0.0.1", "youshengyun.com");
    
}
