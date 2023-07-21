package net.risesoft.kafka;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.entity.Y9logIpDeptMapping;
import net.risesoft.log.entity.Y9logUserHostIpInfo;
import net.risesoft.log.entity.Y9logUserLoginInfo;
import net.risesoft.log.service.Y9logIpDeptMappingService;
import net.risesoft.log.service.Y9logUserHostIpInfoService;
import net.risesoft.log.service.Y9logUserLoginInfoService;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;

@Component
@Slf4j
@RequiredArgsConstructor
public class Y9UserLoginInfoListener {
    
    private final Y9logUserLoginInfoService y9logUserLoginInfoService;
    private final Y9logUserHostIpInfoService y9logUserHostIpInfoService;
    private final Y9logIpDeptMappingService y9logIpDeptMappingService;

    @KafkaListener(topics = {Y9TopicConst.Y9_USERLOGININFO_MESSAGE})
    public void listener(ConsumerRecord<String, String> data) {
        try {
            String msg = data.value();
            HashMap<String, Object> map = Y9JsonUtil.readHashMap(msg);

            String userHostIp = String.valueOf(map.get("userHostIp"));
            if (userHostIp.contains(":")) {
                userHostIp = "127.0.0.1";
            }
            String clientIpSection = userHostIp.substring(0, userHostIp.lastIndexOf("."));
            if (y9logUserLoginInfoService != null) {
                Y9logUserLoginInfo userLoginInfo = new Y9logUserLoginInfo();
                userLoginInfo.setId(String.valueOf(map.get("id")));
                userLoginInfo.setLoginTime(new Date());
                userLoginInfo.setLoginType(String.valueOf(map.get("loginType")));
                userLoginInfo.setUserId(String.valueOf(map.get("userId")));
                userLoginInfo.setUserLoginName(String.valueOf(map.get("userLoginName")));
                userLoginInfo.setUserName(String.valueOf(map.get("userName")));
                userLoginInfo.setUserHostIp(userHostIp);
                userLoginInfo.setUserHostMac(String.valueOf(map.get("userHostMac")));
                userLoginInfo.setUserHostName(String.valueOf(map.get("userHostName")));
                userLoginInfo.setUserHostDiskId(String.valueOf(map.get("userHostDiskId")));
                userLoginInfo.setTenantId(String.valueOf(map.get("tenantId")));
                userLoginInfo.setTenantName(String.valueOf(map.get("tenantName")));
                userLoginInfo.setServerIp(String.valueOf(map.get("serverIp")));
                userLoginInfo.setSuccess(String.valueOf(map.get("success")));
                userLoginInfo.setLogMessage(String.valueOf(map.get("logMessage")));
                userLoginInfo.setBrowserName(String.valueOf(map.get("browserName")));
                userLoginInfo.setBrowserVersion(String.valueOf(map.get("browserVersion")));
                userLoginInfo.setScreenResolution(String.valueOf(map.get("screenResolution")));
                userLoginInfo.setOsName(String.valueOf(map.get("osName")));
                userLoginInfo.setClientIpSection(clientIpSection);
                userLoginInfo.setManagerLevel(String.valueOf(map.get("managerLevel")));
                y9logUserLoginInfoService.save(userLoginInfo);
            }

            /**
             * 保存为录入的ip,部门为"此IP未指定部门"
             */
            if (y9logUserHostIpInfoService != null) {
                List<Y9logUserHostIpInfo> list = y9logUserHostIpInfoService.listByUserHostIp(userHostIp);
                if (list == null || list.size() == 0) {
                    Y9logUserHostIpInfo entity = new Y9logUserHostIpInfo();
                    entity.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    entity.setClientIpSection(clientIpSection);
                    entity.setUserHostIp(userHostIp);
                    y9logUserHostIpInfoService.save(entity);
                }
            }

            if (y9logIpDeptMappingService != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                List<Y9logIpDeptMapping> list = y9logIpDeptMappingService.listByClientIpSection(clientIpSection);
                if (list == null || list.size() == 0) {
                    Y9logIpDeptMapping entity = new Y9logIpDeptMapping();
                    entity.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    entity.setClientIpSection(clientIpSection);
                    entity.setDeptName("此IP未指定部门");
                    entity.setOperator(String.valueOf(map.get("userName")));
                    entity.setSaveTime(sdf.format(new Date()));
                    entity.setStatus(1);
                    entity.setTabIndex(666);
                    entity.setUpdateTime(sdf.format(new Date()));
                    y9logIpDeptMappingService.save(entity);
                } else {
                    for (Y9logIpDeptMapping entity : list) {
                        if (entity.getStatus() == null || entity.getStatus() != 1) {
                            entity.setStatus(1);
                            y9logIpDeptMappingService.save(entity);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
