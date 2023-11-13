package net.risesoft.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import net.risesoft.log.entity.Y9logAccessLog;
import net.risesoft.log.entity.Y9logUserLoginInfo;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.userlogininfo.LoginInfo;

public class AccessLogModelConvertUtil {

    public static List<AccessLog> logESListToModels(List<Y9logAccessLog> sources) {
        List<AccessLog> targets = new ArrayList<AccessLog>();
        for (Y9logAccessLog source : sources) {
            AccessLog target = new AccessLog();
            BeanUtils.copyProperties(source, target);
            targets.add(target);
        }
        return targets;
    }

    public static AccessLog logESToModel(Y9logAccessLog source) {
        AccessLog target = new AccessLog();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static List<LoginInfo> userLoginInfoESListToModels(List<Y9logUserLoginInfo> userLoginInfos) {
        List<LoginInfo> targets = new ArrayList<LoginInfo>();
        for (Y9logUserLoginInfo source : userLoginInfos) {
            LoginInfo target = new LoginInfo();
            BeanUtils.copyProperties(source, target);
            targets.add(target);
        }
        return targets;
    }

    public static LoginInfo userLoginInfoESToModel(Y9logUserLoginInfo userLoginInfo) {
        LoginInfo target = new LoginInfo();
        BeanUtils.copyProperties(userLoginInfo, target);
        return target;
    }

}
