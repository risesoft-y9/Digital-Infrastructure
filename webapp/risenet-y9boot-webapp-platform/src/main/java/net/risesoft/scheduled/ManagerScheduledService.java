package net.risesoft.scheduled;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Manager;
import net.risesoft.log.service.SaveLogInfo4Kafka;
import net.risesoft.model.AccessLog;
import net.risesoft.model.user.UserInfo;
import net.risesoft.model.userlogininfo.LoginInfo;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.util.InetAddressUtil;
import net.risesoft.y9.util.RemoteCallUtil;

@Service
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class ManagerScheduledService {

    private final Y9ManagerService y9ManagerService;
    private final SaveLogInfo4Kafka saveLogInfo4Kafka;

    private final String serverIp = InetAddressUtil.getLocalAddress().getHostAddress();
    private final Y9Properties y9Config;

    /**
     * 每天凌晨2点检查是否登录系统进行审查
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkCycle() {
        long start = System.nanoTime();
        String success = "成功";
        String throwable = "";
        String userAgent = "";
        String hostIp = "";
        String systemName = "";
        LOGGER.info("********************检查保密员审查周期-开始**********************");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> tenantIds = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIds) {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Y9Manager> logPersonList = y9ManagerService.listAll();
            for (Y9Manager logPerson : logPersonList) {
                Integer checkCycle = logPerson.getCheckCycle();
                if (null == checkCycle || 0 == checkCycle) {
                    continue;
                }
                Date checkTime = null;
                systemName = Y9Context.getSystemName();

                String url = y9Config.getCommon().getLogBaseUrl() + "/services/rest/userLoginInfo/getTopByTenantIdAndUserId";
                List<NameValuePair> params = new ArrayList<>();
                params.add(new NameValuePair("personId", logPerson.getId()));
                params.add(new NameValuePair("tenantId", logPerson.getTenantId()));
                params.add(new NameValuePair("page", 1 + ""));
                params.add(new NameValuePair("rows", 2 + ""));
                LoginInfo LoginInfo = RemoteCallUtil.getCallRemoteService(url, params, LoginInfo.class);
                if (LoginInfo != null) {
                    checkTime = LoginInfo.getLoginTime();
                }
                if (checkTime == null) {
                    checkTime = new Date();
                }
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(checkTime);
                    calendar.add(Calendar.DAY_OF_MONTH, checkCycle);
                    long now = System.currentTimeMillis();
                    if (calendar.getTimeInMillis() < now) {
                        long end = System.nanoTime();
                        long elapsedTime = end - start;
                        AccessLog log = new AccessLog();
                        log.setLogLevel("WARN");
                        log.setLogTime(new Date());
                        log.setMethodName("");
                        log.setElapsedTime(String.valueOf(elapsedTime));
                        log.setSuccess(success);
                        log.setLogMessage("已超过" + logPerson.getPwdCycle() + "天未登录系统审查。");
                        log.setThrowable(throwable);
                        log.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                        log.setServerIp(this.serverIp);
                        log.setUserHostIp(hostIp);
                        log.setUserAgent(userAgent);
                        log.setSystemName(systemName);

                        Map<String, Object> map = Y9LoginUserHolder.getMap();
                        if (map != null) {
                            String userHostIp = (String)map.get("userHostIP");
                            if (userHostIp != null) {
                                log.setUserHostIp(userHostIp);
                            }
                            String requestUrl = (String)map.get("requestURL");
                            if (requestUrl != null) {
                                log.setRequestUrl(requestUrl);
                            }
                        }

                        log.setModularName("开源内核");
                        log.setOperateName("安全保密员审查检查");
                        log.setOperateType("审查");

                        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
                        if (null != userInfo) {
                            log.setUserId(userInfo.getPersonId());
                            log.setUserName(userInfo.getName());
                            log.setLoginName(userInfo.getLoginName());
                            log.setTenantId(userInfo.getTenantId());
                            log.setTenantName(Y9LoginUserHolder.getTenantName());
                            log.setDn(userInfo.getDn());
                            log.setGuidPath(userInfo.getGuidPath());
                        }
                        saveLogInfo4Kafka.asyncSave(log);
                    }
                    y9ManagerService.updateCheckTime(logPerson.getId(), checkTime);
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
        LOGGER.info("********************检查保密员审查周期-结束**********************");
    }

    /**
     * 每天凌晨2点检查一下三员密码是不是按时修改
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void pwdCycle() {
        long start = System.nanoTime();
        String success = "成功";
        String throwable = "";
        String userAgent = "";
        String hostIp = "";
        String systemName = "";
        LOGGER.info("********************审计三员密码修改信息-开始**********************");
        List<String> tenantIds = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIds) {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Y9Manager> logPersonList = y9ManagerService.listAll();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Y9Manager logPerson : logPersonList) {
                Integer pwdCycle = logPerson.getPwdCycle();
                try {
                    Date modifyPwdTime = sdf.parse(logPerson.getModifyPwdTime());
                    boolean saveLog = false;
                    if (modifyPwdTime == null) {
                        saveLog = true;
                    } else {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(modifyPwdTime);
                        calendar.add(Calendar.DAY_OF_MONTH, pwdCycle);
                        long now = System.currentTimeMillis();
                        if (calendar.getTimeInMillis() < now) {
                            saveLog = true;
                        }
                    }
                    if (saveLog) {
                        try {
                            systemName = Y9Context.getSystemName();
                        } catch (Exception e) {
                            LOGGER.warn(e.getMessage(), e);
                        }
                        long end = System.nanoTime();
                        long elapsedTime = end - start;
                        AccessLog log = new AccessLog();
                        log.setLogLevel("WARN");
                        log.setLogTime(new Date());
                        log.setMethodName("");
                        log.setElapsedTime(String.valueOf(elapsedTime));
                        log.setSuccess(success);
                        log.setLogMessage("已超过" + logPerson.getPwdCycle() + "天未修改密码。");
                        log.setThrowable(throwable);
                        log.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                        log.setServerIp(this.serverIp);
                        log.setUserHostIp(hostIp);
                        log.setUserAgent(userAgent);
                        log.setSystemName(systemName);

                        Map<String, Object> map = Y9LoginUserHolder.getMap();
                        if (map != null) {
                            String userHostIp = (String)map.get("userHostIP");
                            if (userHostIp != null) {
                                log.setUserHostIp(userHostIp);
                            }
                            String requestUrl = (String)map.get("requestURL");
                            if (requestUrl != null) {
                                log.setRequestUrl(requestUrl);
                            }
                        }

                        log.setModularName("开源内核");
                        log.setOperateName("审查密码修改");
                        log.setOperateType("审查");

                        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
                        if (null != userInfo) {
                            log.setUserId(userInfo.getPersonId());
                            log.setUserName(userInfo.getName());
                            log.setLoginName(userInfo.getLoginName());
                            log.setTenantId(userInfo.getTenantId());
                            log.setTenantName(Y9LoginUserHolder.getTenantName());
                            log.setDn(userInfo.getDn());
                            log.setGuidPath(userInfo.getGuidPath());
                        }
                        saveLogInfo4Kafka.asyncSave(log);
                    }
                } catch (ParseException e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
        LOGGER.info("********************审计三员密码修改信息-结束**********************");
    }
}