package net.risesoft.log.constant;

/**
 * 日志主键定义常量
 *
 * @author guoweijun
 *
 */
public class Y9ESIndexConst {
    public static final String ACCESS_LOG_INDEX = "logindex";

    public static final String IP_DEPT_MAPPING_INDEX = "ipdeptmapping";

    public static final String USERIP_INFO_INDEX = "userhostipinfo";

    public static final String LOGIN_INFO_INDEX = "userlogininfo";

    public static final String LOG_MAPPING_INDEX = "log_mapping_index";

    public static final String CLICKED_APP_INDEX = "appclickindex";

    public static final String COMMONAPP_INDEX = "common_app_for_person";

    private Y9ESIndexConst() {
        throw new IllegalStateException("Utility class");
    }
}
