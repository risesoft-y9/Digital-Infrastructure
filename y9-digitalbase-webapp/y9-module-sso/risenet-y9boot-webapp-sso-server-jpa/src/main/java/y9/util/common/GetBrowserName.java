package y9.util.common;

public class GetBrowserName {

    public static String BROWSER_360SE = "360SE";

    public static String BROWSER_QQBROWSER = "QQBrowser";

    public static String getBrowserName(String userAgent) {
        if (userAgent.contains(BROWSER_360SE)) {
            return BROWSER_360SE;
        } else if (userAgent.contains(BROWSER_QQBROWSER)) {
            return BROWSER_QQBROWSER;
        }
        return UserAgentUtil.getUserAgentInfo(userAgent).getUaFamily();
    }
}
