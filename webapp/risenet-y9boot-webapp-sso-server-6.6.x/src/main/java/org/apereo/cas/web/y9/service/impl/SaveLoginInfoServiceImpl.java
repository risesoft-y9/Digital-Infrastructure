package org.apereo.cas.web.y9.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.web.y9.service.SaveLoginInfoService;
import org.apereo.cas.web.y9.util.InetAddressUtil;
import org.apereo.cas.web.y9.util.common.UserAgentUtil;
import org.apereo.cas.web.y9.util.json.Y9JacksonUtil;
import org.apereo.cas.web.y9.y9user.Y9User;
import org.apereo.cas.web.y9.y9user.Y9UserDao;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cz.mallat.uasparser.UserAgentInfo;

@Service(value = "saveLoginInfoService")
@Slf4j
@RequiredArgsConstructor
public class SaveLoginInfoServiceImpl implements SaveLoginInfoService {

    public static String SSO_SERVER_IP = "";

    static {
        SSO_SERVER_IP = InetAddressUtil.getLocalAddress().getHostAddress();
    }

    private final Y9UserDao y9UserDao;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Async
    @Override
    public void SaveLoginInfo(RememberMeUsernamePasswordCredential credential, String success, String logMessage) {
        try {
            String deptId = credential.getDeptId();
            String loginType = credential.getLoginType();
            String tenantShortName = credential.getTenantShortName();
            String userLoginName = credential.getUsername();
            String userHostIP = credential.getClientIp();
            String userAgent = credential.getUserAgent();
            String screenResolution = credential.getScreenDimension();
            String userHostMAC = credential.getClientMac();
            String userHostName = credential.getClientHostName();
            String userHostDiskId = credential.getClientDiskId();

            String tenantName = "";
            String personId = "";
            String tenantId = "";
            String userName = "";
            String managerLevel = "0";
            if (!"127.0.0.1".equals(userHostIP)) {
                UserAgentInfo uaInfo = UserAgentUtil.getUserAgentInfo(userAgent);

                List<Y9User> users = null;
                if ("mobile".equals(loginType)) {
                    if (StringUtils.hasText(deptId)) {
                        users =
                            y9UserDao.findByTenantShortNameAndMobileAndParentId(tenantShortName, userLoginName, deptId);
                    } else {
                        users = y9UserDao.findByTenantShortNameAndMobileAndOriginal(tenantShortName, userLoginName,
                            Boolean.TRUE);
                    }
                } else {
                    if (StringUtils.hasText(deptId)) {
                        users = y9UserDao.findByTenantShortNameAndLoginNameAndParentId(tenantShortName, userLoginName,
                            deptId);
                    } else {
                        users = y9UserDao.findByTenantShortNameAndLoginNameAndOriginal(tenantShortName, userLoginName,
                            Boolean.TRUE);
                    }
                }
                if (users != null && users.size() > 0) {
                    Y9User user = users.get(0);
                    tenantName = user.getTenantName();
                    personId = user.getPersonId();
                    tenantId = user.getTenantId();
                    userName = user.getName();
                    managerLevel = String.valueOf(user.getManagerLevel());
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String loginTime = sdf.format(new Date());
                String browser = UserAgentUtil.getBrowserName(userAgent);

                HashMap<String, Object> map = new HashMap<>();
                map.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
                map.put("loginTime", loginTime);
                map.put("loginType", loginType);
                map.put("userId", personId);
                map.put("userLoginName", userLoginName);
                map.put("userName", userName);
                map.put("userHostIp", userHostIP);
                map.put("userHostMac", userHostMAC);
                map.put("userHostName", userHostName);
                map.put("userHostDiskId", userHostDiskId);
                map.put("tenantId", tenantId);
                map.put("tenantName", tenantName);
                map.put("serverIp", SSO_SERVER_IP);
                map.put("success", success);
                map.put("logMessage", logMessage);
                map.put("browserName", browser);
                map.put("browserVersion", uaInfo.getBrowserVersionInfo());
                map.put("screenResolution", screenResolution);
                map.put("osName", uaInfo.getOsName());
                map.put("managerLevel", managerLevel);
                String jsonString = Y9JacksonUtil.writeValueAsString(map);
                kafkaTemplate.send("y9_userLoginInfo_message", jsonString);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
