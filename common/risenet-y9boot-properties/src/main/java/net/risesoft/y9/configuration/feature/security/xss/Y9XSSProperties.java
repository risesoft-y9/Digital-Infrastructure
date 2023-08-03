package net.risesoft.y9.configuration.feature.security.xss;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * xss 配置
 * 
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9XSSProperties {

    /**
     * 是否开启xss过滤 默认开启
     */
    private boolean enabled = true;

    /**
     * 忽略的请求参数名
     */
    private List<String> ignoreParam = Arrays.asList("text", "txt", "formJson", "fieldJson");
}
