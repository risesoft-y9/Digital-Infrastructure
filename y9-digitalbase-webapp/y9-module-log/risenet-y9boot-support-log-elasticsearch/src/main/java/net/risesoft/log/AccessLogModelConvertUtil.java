package net.risesoft.log;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import net.risesoft.log.entity.Y9logAccessLog;
import net.risesoft.log.entity.Y9logUserLoginInfo;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.userlogininfo.LoginInfo;

/**
 * 日志转换工具类
 *
 * @author guoweijun
 * @author mengjuhua
 *
 */
public class AccessLogModelConvertUtil {

    public static List<AccessLog> logEsListToModels(List<Y9logAccessLog> sources) {
        List<AccessLog> targets = new ArrayList<AccessLog>();
        for (Y9logAccessLog source : sources) {
            AccessLog target = new AccessLog();
            BeanUtils.copyProperties(source, target);
            targets.add(target);
        }
        return targets;
    }

    public static AccessLog logEsToModel(Y9logAccessLog source) {
        AccessLog target = new AccessLog();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static List<LoginInfo> userLoginInfoEsListToModels(List<Y9logUserLoginInfo> userLoginInfos) {
        List<LoginInfo> targets = new ArrayList<LoginInfo>();
        for (Y9logUserLoginInfo source : userLoginInfos) {
            LoginInfo target = new LoginInfo();
            BeanUtils.copyProperties(source, target);
            targets.add(target);
        }
        return targets;
    }

    public static LoginInfo userLoginInfoEsToModel(Y9logUserLoginInfo userLoginInfo) {
        LoginInfo target = new LoginInfo();
        BeanUtils.copyProperties(userLoginInfo, target);
        return target;
    }

    private AccessLogModelConvertUtil() {}

}
