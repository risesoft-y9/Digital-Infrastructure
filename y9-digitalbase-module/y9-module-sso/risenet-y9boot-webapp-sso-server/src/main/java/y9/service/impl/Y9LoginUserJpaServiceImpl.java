package y9.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import y9.entity.Y9LoginUser;
import y9.entity.Y9User;
import y9.repository.Y9LoginUserRepository;
import y9.repository.Y9UserRepository;
import y9.service.Y9LoginUserService;
import y9.util.InetAddressUtil;
import y9.util.Y9Context;
import y9.util.common.UserAgentUtil;

import cz.mallat.uasparser.UserAgentInfo;

@Slf4j
@RequiredArgsConstructor
public class Y9LoginUserJpaServiceImpl implements Y9LoginUserService {

    public static String SSO_SERVER_IP = InetAddressUtil.getLocalAddress().getHostAddress();

    private final Y9LoginUserRepository y9LoginUserRepository;
    private final Y9UserRepository y9UserRepository;

    @Override
    public void save(UsernamePasswordCredential credential, String success, String logMessage) {
        try {
            String userLoginName = credential.getUsername();
            Map<String, Object> customFields = credential.getCustomFields();
            String deptId = (String)customFields.get("deptId");
            String loginType = (String)customFields.get("loginType");
            String tenantShortName = (String)customFields.get("tenantShortName");
            String userAgent = (String)customFields.get("userAgent");
            String screenResolution = (String)customFields.get("screenDimension");
            String userHostIP = (String)customFields.get("userHostIP");
            String userHostMAC = (String)customFields.get("userHostMAC");
            String userHostName = (String)customFields.get("userHostName");
            String userHostDiskId = (String)customFields.get("userHostDiskId");

            String tenantName = "";
            String personId = "";
            String tenantId = "";
            String userName = "";
            String managerLevel = "0";
            if (!"127.0.0.1".equals(userHostIP)) {
                UserAgentInfo uaInfo = UserAgentUtil.getUserAgentInfo(userAgent);
                String browser = UserAgentUtil.getBrowserName(userAgent);

                List<Y9User> users = null;
                if ("mobile".equals(loginType)) {
                    if (StringUtils.hasText(deptId)) {
                        users = y9UserRepository.findByTenantShortNameAndMobileAndParentId(tenantShortName,
                            userLoginName, deptId);
                    } else {
                        users = y9UserRepository.findByTenantShortNameAndMobileAndOriginal(tenantShortName,
                            userLoginName, Boolean.TRUE);
                    }
                } else {
                    if (StringUtils.hasText(deptId)) {
                        users = y9UserRepository.findByTenantShortNameAndLoginNameAndParentId(tenantShortName,
                            userLoginName, deptId);
                    } else {
                        users = y9UserRepository.findByTenantShortNameAndLoginNameAndOriginal(tenantShortName,
                            userLoginName, Boolean.TRUE);
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

                Y9LoginUser user = new Y9LoginUser();
                user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                user.setLoginTime(Instant.now());
                user.setLoginType(loginType);
                user.setUserId(personId);
                user.setUserLoginName(userLoginName);
                user.setUserName(userName);
                user.setUserHostIp(userHostIP);
                user.setUserHostMac(userHostMAC);
                user.setUserHostName(userHostName);
                user.setUserHostDiskId(userHostDiskId);
                user.setTenantId(tenantId);
                user.setTenantName(tenantName);
                user.setServerIp(SSO_SERVER_IP);
                user.setSuccess(success);
                user.setLogMessage(logMessage);
                user.setBrowserName(browser);
                user.setBrowserVersion(uaInfo.getBrowserVersionInfo());
                user.setScreenResolution(screenResolution);
                user.setOsName(uaInfo.getOsName());
                user.setManagerLevel(managerLevel);
                y9LoginUserRepository.save(user);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
