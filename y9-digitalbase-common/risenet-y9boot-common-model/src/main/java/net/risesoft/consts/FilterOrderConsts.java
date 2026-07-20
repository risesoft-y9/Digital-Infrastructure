package net.risesoft.consts;

import org.springframework.core.Ordered;

/**
 * 过滤器顺序常量
 *
 * @author shidaobang
 * @date 2024/11/14
 * @since 9.6.8
 */
public class FilterOrderConsts {

    public static final int CORS_ORDER = Ordered.HIGHEST_PRECEDENCE;

    public static final int CSRF_ORDER = Ordered.HIGHEST_PRECEDENCE + 10;

    public static final int XSS_ORDER = Ordered.HIGHEST_PRECEDENCE + 20;

    public static final int SQL_INJECTION_ORDER = Ordered.HIGHEST_PRECEDENCE + 30;

    public static final int MULTI_TENANT_ORDER = Ordered.HIGHEST_PRECEDENCE + 31;

    public static final int OAUTH2_RESOURCE_ORDER = Ordered.HIGHEST_PRECEDENCE + 40;

    public static final int API_BLACK_LIST_ORDER = Ordered.HIGHEST_PRECEDENCE + 40;

    public static final int API_WHITE_LIST_ORDER = Ordered.HIGHEST_PRECEDENCE + 50;

    public static final int API_TOKEN_ORDER = Ordered.HIGHEST_PRECEDENCE + 60;

    public static final int API_SIGN_ORDER = Ordered.HIGHEST_PRECEDENCE + 70;

    public static final int USER_ONLINE_ORDER = Ordered.HIGHEST_PRECEDENCE + 80;

    public static final int LOG_ORDER = 5;

}
