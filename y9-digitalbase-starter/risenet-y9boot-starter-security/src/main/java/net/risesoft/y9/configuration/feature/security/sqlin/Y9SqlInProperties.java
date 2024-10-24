package net.risesoft.y9.configuration.feature.security.sqlin;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * SQLIN 配置
 * 
 * @author mengjuhua
 * @date 2024/03/04
 */
@Getter
@Setter
public class Y9SqlInProperties {
    /**
     * 是否开启SQlIn过滤 默认开启
     */
    private boolean enabled = true;

    /**
     * 跳过检查的请求
     */
    private String skipUrl = "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*";

    /**
     * 忽略的请求参数名
     */
    private List<String> ignoreParam = Arrays.asList("text", "txt", "formJson", "fieldJson");
}
