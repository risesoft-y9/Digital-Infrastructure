package y9.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apereo.cas.authentication.credential.AbstractCredential;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import y9.authen.noview.Y9Credential;
import y9.entity.Y9LoginUser;
import y9.entity.Y9User;
import y9.repository.Y9LoginUserRepository;
import y9.repository.Y9UserRepository;
import y9.service.Y9LoginUserService;
import y9.util.InetAddressUtil;
import y9.util.Y9Context;
import y9.util.common.UserAgentUtil;
import y9.util.json.Y9JacksonUtil;

import cz.mallat.uasparser.UserAgentInfo;

@Service("y9LoginUserService")
@Slf4j
@RequiredArgsConstructor
public class Y9LoginUserServiceImpl implements Y9LoginUserService {

    public static String SSO_SERVER_IP = InetAddressUtil.getLocalAddress().getHostAddress();

    private final Y9LoginUserRepository y9LoginUserRepository;
    private final Y9UserRepository y9UserRepository;

    @Override
    public void save(AbstractCredential credential, String success, String logMessage) {
    	String userLoginName;
        String deptId;
        String loginType;
        String tenantShortName;
        String userHostIP;
        String userAgent;
        String screenResolution;
        String userHostMAC;
        String userHostName;
        String userHostDiskId;
        
        try {
        	if(credential instanceof Y9Credential) {
        		Y9Credential y9Credential = (Y9Credential)credential;
        		userLoginName = y9Credential.getUsername();
        		deptId = y9Credential.getDeptId();
                loginType = y9Credential.getLoginType();
                tenantShortName = y9Credential.getTenantShortName();
                userHostIP = y9Credential.getClientIp();
                userAgent = y9Credential.getUserAgent();
                screenResolution = y9Credential.getScreenDimension();
                userHostMAC = y9Credential.getClientMac();
                userHostName = y9Credential.getClientHostName();
                userHostDiskId = y9Credential.getClientDiskId();
        	}else {
        		RememberMeUsernamePasswordCredential y9Credential = (RememberMeUsernamePasswordCredential)credential;
        		userLoginName = y9Credential.getUsername();
                Map<String, Object> customFields = y9Credential.getCustomFields();
                deptId = (String)customFields.get("deptId");
                loginType = (String)customFields.get("loginType");
                tenantShortName = (String)customFields.get("tenantShortName");
                userHostIP = (String)customFields.get("userHostIP");
                userAgent = (String)customFields.get("userAgent");
                screenResolution = (String)customFields.get("screenResolution");
                userHostMAC = (String)customFields.get("userHostMAC");
                userHostName = (String)customFields.get("userHostName");
                userHostDiskId = (String)customFields.get("userHostDiskId");
        	}            

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
                String loginInfoSaveTarget = Y9Context.getProperty("y9.loginInfoSaveTarget");
                if ("jpa".equals(loginInfoSaveTarget)) {
                    y9LoginUserRepository.save(user);
                } else {
                    String jsonString = Y9JacksonUtil.writeValueAsString(user);
                    // kafkaTemplate.send("y9_userLoginInfo_message", jsonString);
                    LOGGER.info("保存登录日志成功至Kafka成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
