package net.risesoft.scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import net.risesoft.api.log.UserLoginInfoApi;
import net.risesoft.entity.Y9Manager;
import net.risesoft.entity.Y9Organization;
import net.risesoft.enums.platform.TenantTypeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.service.AccessLogPusher;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.userlogininfo.LoginInfo;
import net.risesoft.service.identity.IdentityResourceCalculator;
import net.risesoft.service.identity.IdentityRoleCalculator;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.InetAddressUtil;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.service.tenant.Y9TenantService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledTask {
    private final String serverIp = InetAddressUtil.getLocalAddress().getHostAddress();

    private final UserLoginInfoApi userLoginInfoApi;
    private final Y9ManagerService y9ManagerService;
    private final Y9OrganizationService y9OrganizationService;
    private final Y9TenantService y9TenantService;

    private final IdentityResourceCalculator identityResourceCalculator;
    private final IdentityRoleCalculator identityRoleCalculator;

    private AccessLogPusher accessLogPusher;

    /**
     * 每天凌晨1点检查是否登录系统进行审查
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @SchedulerLock(name = "checkManagerLogReviewLock", lockAtLeastFor = "PT30M")
    public void checkManagerLogReview() {
        LOGGER.info("********************检查三员审查情况-开始**********************");

        long start = System.nanoTime();
        String systemName = Y9Context.getSystemName();
        List<String> tenantIds = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIds) {
            Y9LoginUserHolder.setTenantId(tenantId);
            LOGGER.debug("检查租户[{}]三员审查情况", tenantId);

            List<Y9Manager> y9ManagerList = y9ManagerService.listAll();
            for (Y9Manager y9Manager : y9ManagerList) {
                int reviewLogCycle = y9ManagerService.getReviewLogCycle(y9Manager.getManagerLevel());

                if (0 == reviewLogCycle) {
                    continue;
                }

                LoginInfo loginInfo =
                    userLoginInfoApi.getTopByTenantIdAndUserId(y9Manager.getTenantId(), y9Manager.getId());
                Date checkTime = loginInfo == null ? new Date() : loginInfo.getLoginTime();
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(checkTime);
                    calendar.add(Calendar.DAY_OF_MONTH, reviewLogCycle);
                    long now = System.currentTimeMillis();
                    if (calendar.getTimeInMillis() < now) {
                        long end = System.nanoTime();
                        long elapsedTime = end - start;
                        AccessLog log = new AccessLog();
                        log.setLogLevel(LogLevelEnum.MANAGERLOG.toString());
                        log.setLogTime(new Date());
                        log.setElapsedTime(String.valueOf(elapsedTime));
                        log.setSuccess("成功");
                        log.setManagerLevel(y9Manager.getManagerLevel().getValue().toString());
                        log.setLogMessage(y9Manager.getName() + "已超过" + reviewLogCycle + "天未登录系统审查。");
                        log.setTenantId(tenantId);
                        log.setId(Y9IdGenerator.genId());
                        log.setServerIp(this.serverIp);
                        log.setUserHostIp(this.serverIp);
                        log.setSystemName(systemName);
                        log.setMethodName("checkManagerLogReview");
                        log.setModularName("数字底座");
                        log.setOperateName("检查三员审查情况");
                        log.setOperateType(OperationTypeEnum.CHECK.getValue());

                        if (accessLogPusher != null) {
                            accessLogPusher.push(log);
                        }
                    }
                    y9ManagerService.updateCheckTime(y9Manager.getId(), checkTime);
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }

        LOGGER.info("********************检查三员审查情况-结束**********************");
    }

    /**
     * 每天凌晨1点检查三员密码是不是按时修改
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @SchedulerLock(name = "checkManagerPasswordModificationLock", lockAtLeastFor = "PT30M")
    public void checkManagerPasswordModification() {
        LOGGER.info("********************检查三员密码修改情况-开始**********************");

        long start = System.nanoTime();
        String systemName = Y9Context.getSystemName();
        List<String> tenantIds = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIds) {
            Y9LoginUserHolder.setTenantId(tenantId);
            LOGGER.debug("检查租户[{}]三员密码修改情况", tenantId);

            List<Y9Manager> y9ManagerList = y9ManagerService.listAll();
            for (Y9Manager y9Manager : y9ManagerList) {
                int modifyPasswordCycle = y9ManagerService.getPasswordModifiedCycle(y9Manager.getManagerLevel());
                Date modifyPwdTime = y9Manager.getLastModifyPasswordTime();
                boolean saveLog = false;
                if (modifyPwdTime == null) {
                    saveLog = true;
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(modifyPwdTime);
                    calendar.add(Calendar.DAY_OF_MONTH, modifyPasswordCycle);
                    long now = System.currentTimeMillis();
                    if (calendar.getTimeInMillis() < now) {
                        saveLog = true;
                    }
                }
                if (saveLog) {
                    long end = System.nanoTime();
                    long elapsedTime = end - start;
                    AccessLog log = new AccessLog();
                    log.setLogLevel(LogLevelEnum.MANAGERLOG.toString());
                    log.setLogTime(new Date());
                    log.setElapsedTime(String.valueOf(elapsedTime));
                    log.setSuccess("成功");
                    log.setManagerLevel(y9Manager.getManagerLevel().getValue().toString());
                    log.setLogMessage(y9Manager.getName() + "已超过" + modifyPasswordCycle + "天未修改密码。");
                    log.setTenantId(tenantId);
                    log.setId(Y9IdGenerator.genId());
                    log.setServerIp(this.serverIp);
                    log.setUserHostIp(this.serverIp);
                    log.setSystemName(systemName);
                    log.setMethodName("checkManagerPasswordModification");
                    log.setModularName("数字底座");
                    log.setOperateName("检查三员密码修改");
                    log.setOperateType(OperationTypeEnum.CHECK.getValue());

                    if (accessLogPusher != null) {
                        accessLogPusher.push(log);
                    }
                }
            }
        }

        LOGGER.info("********************检查三员密码修改情况-结束**********************");
    }

    @Autowired(required = false)
    public void setAccessLogPusher(AccessLogPusher accessLogPusher) {
        this.accessLogPusher = accessLogPusher;
    }

    /**
     * 每天凌晨2点同步授权主体的资源权限
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @SchedulerLock(name = "syncIdentityResourceLock", lockAtLeastFor = "PT30M")
    public void syncIdentityResource() {
        List<Y9Tenant> y9TenantList = y9TenantService.listByTenantType(TenantTypeEnum.TENANT);
        for (Y9Tenant y9Tenant : y9TenantList) {
            Y9LoginUserHolder.setTenantId(y9Tenant.getId());
            LOGGER.debug("同步租户[{}]授权主体的资源权限", y9Tenant.getId());

            for (Y9Organization y9Organization : y9OrganizationService.list()) {
                identityResourceCalculator.recalculateByOrgUnitId(y9Organization.getId());
            }
        }

        LOGGER.info("同步授权主体的资源权限结束");
    }

    /**
     * 每天凌晨4点同步授权主体的角色
     */
    @Scheduled(cron = "0 0 4 * * ?")
    @SchedulerLock(name = "syncIdentityRoleLock", lockAtLeastFor = "PT30M")
    public void syncIdentityRole() {
        List<Y9Tenant> y9TenantList = y9TenantService.listByTenantType(TenantTypeEnum.TENANT);
        for (Y9Tenant y9Tenant : y9TenantList) {
            Y9LoginUserHolder.setTenantId(y9Tenant.getId());
            LOGGER.debug("同步租户[{}]授权主体的角色", y9Tenant.getId());

            for (Y9Organization y9Organization : y9OrganizationService.list()) {
                identityRoleCalculator.recalculateByOrgUnitId(y9Organization.getId());
            }
        }

        LOGGER.info("同步授权主体的角色结束");
    }
}