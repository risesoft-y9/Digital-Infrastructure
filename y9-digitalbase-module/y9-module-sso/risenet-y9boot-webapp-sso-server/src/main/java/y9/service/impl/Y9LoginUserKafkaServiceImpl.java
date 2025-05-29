package y9.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import y9.entity.Y9LoginUser;
import y9.entity.Y9User;
import y9.repository.Y9UserRepository;
import y9.service.Y9LoginUserService;
import y9.util.Y9Context;
import y9.util.common.UserAgentUtil;
import y9.util.json.Y9JacksonUtil;

import cz.mallat.uasparser.UserAgentInfo;

@Slf4j
@RequiredArgsConstructor
public class Y9LoginUserKafkaServiceImpl implements Y9LoginUserService {

    private final Y9UserRepository y9UserRepository;
    private final KafkaTemplate<String, Object> y9KafkaTemplate;

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
            UserAgentInfo uaInfo = UserAgentUtil.getUserAgentInfo(userAgent);
            String browser = UserAgentUtil.getBrowserName(userAgent);

            List<Y9User> users = null;
            if ("mobile".equals(loginType)) {
                if (StringUtils.hasText(deptId)) {
                    users = y9UserRepository.findByTenantShortNameAndMobileAndParentId(tenantShortName, userLoginName,
                        deptId);
                } else {
                    users = y9UserRepository.findByTenantShortNameAndMobileAndOriginal(tenantShortName, userLoginName,
                        Boolean.TRUE);
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
            if (users != null && !users.isEmpty()) {
                Y9User user = users.get(0);
                tenantName = user.getTenantName();
                personId = user.getPersonId();
                tenantId = user.getTenantId();
                userName = user.getName();
                managerLevel = String.valueOf(user.getManagerLevel());
            }

            Y9LoginUser user = new Y9LoginUser();
            user.setId(UUID.randomUUID().toString().replace("-", ""));
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
            user.setServerIp(Y9Context.getHostIp());
            user.setSuccess(success);
            user.setLogMessage(logMessage);
            user.setBrowserName(browser);
            user.setBrowserVersion(uaInfo.getBrowserVersionInfo());
            user.setScreenResolution(screenResolution);
            user.setOsName(uaInfo.getOsName());
            user.setManagerLevel(managerLevel);
            String jsonString = Y9JacksonUtil.writeValueAsString(user);
            y9KafkaTemplate.send("y9_userLoginInfo_message", jsonString);
            LOGGER.info("保存登录日志成功至Kafka成功,登录信息：{}", user.getLogMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
