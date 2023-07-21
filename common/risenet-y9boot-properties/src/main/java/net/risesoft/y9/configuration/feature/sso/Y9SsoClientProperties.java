package net.risesoft.y9.configuration.feature.sso;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * sso客户配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9SsoClientProperties {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 登出路径
     */
    private String logoutUrl = "https://yun.szlh.gov.cn/sso/logout?service=http://yun.szlh.gov.cn";

    /**
     * 登录路径
     */
    private String loginUrl = "https://yun.szlh.gov.cn/sso/login?service=http://yun.szlh.gov.cn";

    /**
     * cas服务器url前缀
     */
    private String casServerUrlPrefix;

    /**
     * cas服务器url前缀进行验证
     */
    private String casServerUrlPrefixForValidation;

    /**
     * 服务器名称
     */
    private String serverName;

    /**
     * 登出过滤过滤器顺序
     */
    private Integer singleSignOutFilterOrder = 1;

    /**
     * 忽略url正则判断类
     */
    private String ignoreUrlPatternType = "net.risesoft.filter.RiseContainMatcher";

    /**
     * 忽略url正则
     */
    private String ignorePattern = "/static,/ssoProxyCallback";

    /**
     * cas过滤url正则
     */
    private List<String> casFilterUrlPatterns = new ArrayList<>();

    /**
     * 是否保存日志消息
     */
    private boolean saveLogMessage = false;

    /**
     * 是否保存在线消息
     */
    private boolean saveOnlineMessage = true;

    /**
     * 网关
     */
    private Boolean gateway = false;

    /**
     * 使用会话
     */
    private Boolean useSession = true;

    /**
     * 验证后重定向
     */
    private Boolean redirectAfterValidation = true;

    /**
     * 接受任何代理
     */
    private Boolean acceptAnyProxy = false;

    /**
     * 允许代理链
     */
    private List<String> allowedProxyChains = new ArrayList<>();

    /**
     * 代理回调 url
     */
    private String proxyCallbackUrl;

    /**
     * 代理受体 url
     */
    private String proxyReceptorUrl;

    /**
     * 验证类型
     */
    private String validationType = "CAS3";
    
}
