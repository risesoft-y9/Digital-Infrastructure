package y9.util.common;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import cz.mallat.uasparser.UserAgentInfo;

/**
 * 
 * @author dingzhaojun
 * @author mengjuhua
 * @author shidaobang
 * @author qinman
 * @date 2022/12/29
 */
@Slf4j
public class UserAgentUtil {
    static UASparser uasParser = null;
    static {
        try {
            uasParser = new UASparser(OnlineUpdater.getVendoredInputStream());
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public static boolean checkBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String browserName = GetBrowserName.getBrowserName(userAgent);
        String ieStr = "IE";
        if (ieStr.equals(browserName.toUpperCase())) {
            if (userAgent.indexOf("MSIE 9") > 0 || userAgent.indexOf("MSIE 10") > 0 || userAgent.indexOf("like Gecko") > 0) {
                return true;
            }
        }
        return false;
    }

    public static String getBrowserName(String userAgent) {
        if (null == userAgent) {
            return "";
        }
        String browserName = GetBrowserName.getBrowserName(userAgent);
        if (userAgent.indexOf("msie 9") > 0 || userAgent.indexOf("msie 10") > 0 || userAgent.indexOf("rv:11") > 0 || userAgent.indexOf("msie 6") > 0 || userAgent.indexOf("msie 7") > 0
            || userAgent.indexOf("msie 8") > 0) {
            return "IE";
        }
        return browserName;
    }

    public static UserAgentInfo getUserAgentInfo(String str) {
        try {
            UserAgentInfo userAgentInfo = UserAgentUtil.uasParser.parse(str);
            return userAgentInfo;
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }
}