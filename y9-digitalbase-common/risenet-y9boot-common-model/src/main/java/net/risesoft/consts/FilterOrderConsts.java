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

    public static final int CSRF_ORDER = Ordered.HIGHEST_PRECEDENCE + 1;

    public static final int XSS_ORDER = Ordered.HIGHEST_PRECEDENCE + 2;

    public static final int SQL_INJECTION_ORDER = Ordered.HIGHEST_PRECEDENCE + 3;

    public static final int OAUTH2_RESOURCE_ORDER = Ordered.HIGHEST_PRECEDENCE + 4;

    public static final int API_BLACK_LIST_ORDER = Ordered.HIGHEST_PRECEDENCE + 4;

    public static final int API_WHITE_LIST_ORDER = Ordered.HIGHEST_PRECEDENCE + 5;

    public static final int API_TOKEN_ORDER = Ordered.HIGHEST_PRECEDENCE + 6;

    public static final int API_SIGN_ORDER = Ordered.HIGHEST_PRECEDENCE + 6;

    public static final int LOG_ORDER = 5;

}
