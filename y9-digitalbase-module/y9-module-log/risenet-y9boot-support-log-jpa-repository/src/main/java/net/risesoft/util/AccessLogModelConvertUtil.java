package net.risesoft.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.userlogininfo.LoginInfo;
import net.risesoft.y9public.entity.Y9logAccessLog;
import net.risesoft.y9public.entity.Y9logFlowableAccessLog;
import net.risesoft.y9public.entity.Y9logUserLoginInfo;

/**
 * 日志转换工具类
 *
 * @author guoweijun
 * @author mengjuhua
 *
 */
public class AccessLogModelConvertUtil {

    private AccessLogModelConvertUtil() {}

    public static List<AccessLog> logEsListToModels(List<Y9logAccessLog> sources) {
        List<AccessLog> targets = new ArrayList<AccessLog>();
        for (Y9logAccessLog source : sources) {
            AccessLog target = new AccessLog();
            BeanUtils.copyProperties(source, target);
            targets.add(target);
        }
        return targets;
    }

    public static List<FlowableAccessLog> flowableLogEsListToModels(List<Y9logFlowableAccessLog> sources) {
        List<FlowableAccessLog> targets = new ArrayList<>();
        for (Y9logFlowableAccessLog source : sources) {
            FlowableAccessLog target = new FlowableAccessLog();
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

}
