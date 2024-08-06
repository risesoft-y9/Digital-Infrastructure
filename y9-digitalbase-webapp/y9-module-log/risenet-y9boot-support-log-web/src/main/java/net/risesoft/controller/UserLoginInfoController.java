package net.risesoft.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9logUserLoginInfo;
import net.risesoft.y9public.service.Y9logUserLoginInfoService;

/**
 * 登录日志管理
 *
 * @author mengjuhua
 * @author guoweijun
 * @author shidaobang
 * @author yihong
 *
 */
@RestController
@RequestMapping(value = "/admin/userLoginInfo")
@RequiredArgsConstructor
public class UserLoginInfoController {

    private final Y9logUserLoginInfoService userLoginInfoService;

    /**
     * 获取安全审计员登录日志
     *
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "查看安全审计员登录日志", logLevel = LogLevelEnum.MANAGERLOG)
    @RequestMapping(value = "/pageByAuditManagers")
    public Y9Page<Y9logUserLoginInfo> pageByAuditManagers(Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.AUDIT_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_AUDIT_MANAGER.getValue();
        }
        Page<Y9logUserLoginInfo> pageList = userLoginInfoService.pageByTenantIdAndManagerLevel(tenantId,
            String.valueOf(managerLevel), pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取日志分页列表成功");
    }

    /**
     * 查看安全保密员登录日志
     *
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "查看安全保密员登录日志", logLevel = LogLevelEnum.MANAGERLOG)
    @RequestMapping(value = "/pageBySecurityManagers")
    public Y9Page<Y9logUserLoginInfo> pageBySecurityManagers(Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.SECURITY_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_SECURITY_MANAGER.getValue();
        }
        Page<Y9logUserLoginInfo> pageList = userLoginInfoService.pageByTenantIdAndManagerLevel(tenantId,
            String.valueOf(managerLevel), pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取日志分页列表成功");
    }

    /**
     * 查看系统管理员登录日志
     *
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "查看系统管理员登录日志", logLevel = LogLevelEnum.MANAGERLOG)
    @RequestMapping(value = "/pageBySystemManagers")
    public Y9Page<Y9logUserLoginInfo> pageBySystemManagers(Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.SYSTEM_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_SYSTEM_MANAGER.getValue();
        }
        Page<Y9logUserLoginInfo> pageList = userLoginInfoService.pageByTenantIdAndManagerLevel(tenantId,
            String.valueOf(managerLevel), pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取日志分页列表成功");
    }

    /**
     * 获取普通用户登录日志
     *
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "查看用户登录日志", logLevel = LogLevelEnum.MANAGERLOG)
    @RequestMapping(value = "/pageByUsers")
    public Y9Page<Y9logUserLoginInfo> pageByUsers(Y9PageQuery pageQuery) {
        Page<Y9logUserLoginInfo> pageList =
            userLoginInfoService.pageByTenantIdAndManagerLevel(Y9LoginUserHolder.getTenantId(),
                String.valueOf(ManagerLevelEnum.GENERAL_USER.getValue()), pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取日志分页列表成功");
    }

    /**
     * 根据条件查询安全审计员登录日志
     *
     * @param loginInfoModel 搜索条件
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "查询安全审计员登录日志", logLevel = LogLevelEnum.MANAGERLOG)
    @RequestMapping(value = "/searchAuditManagers")
    public Y9Page<Y9logUserLoginInfo> searchAuditManagers(LogInfoModel loginInfoModel, Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.AUDIT_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_AUDIT_MANAGER.getValue();
        }
        Y9Page<Y9logUserLoginInfo> pageList = userLoginInfoService.searchQuery(tenantId, String.valueOf(managerLevel),
            loginInfoModel, pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotal(), pageList.getRows(),
            "获取日志分页列表成功");
    }

    /**
     * 根据条件查询安全保密员登录日志
     *
     * @param loginInfoModel 搜索条件
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "查询安全保密员登录日志", logLevel = LogLevelEnum.MANAGERLOG)
    @RequestMapping(value = "/searchSecurityManagers")
    public Y9Page<Y9logUserLoginInfo> searchSecurityManagers(LogInfoModel loginInfoModel, Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.SECURITY_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_SECURITY_MANAGER.getValue();
        }
        Y9Page<Y9logUserLoginInfo> pageList = userLoginInfoService.searchQuery(tenantId, String.valueOf(managerLevel),
            loginInfoModel, pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotal(), pageList.getRows(),
            "获取日志分页列表成功");
    }

    /**
     * 根据条件查询系统管理员登录日志
     *
     * @param loginInfoModel 搜索条件
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "查询系统管理员登录日志", logLevel = LogLevelEnum.MANAGERLOG)
    @RequestMapping(value = "/searchSystemManagers")
    public Y9Page<Y9logUserLoginInfo> searchSystemManagers(LogInfoModel loginInfoModel, Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Integer managerLevel = ManagerLevelEnum.SYSTEM_MANAGER.getValue();
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            managerLevel = ManagerLevelEnum.OPERATION_SYSTEM_MANAGER.getValue();
        }
        Y9Page<Y9logUserLoginInfo> pageList = userLoginInfoService.searchQuery(tenantId, String.valueOf(managerLevel),
            loginInfoModel, pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotal(), pageList.getRows(),
            "获取日志分页列表成功");
    }

    /**
     * 根据条件查询普通用户登录日志
     *
     * @param loginInfoModel 搜索条件
     * @param pageQuery 分页信息
     * @return {@code Y9Page<Y9logUserLoginInfo>}
     */
    @RiseLog(moduleName = "日志系统", operationName = "查询用户登录日志", logLevel = LogLevelEnum.MANAGERLOG)
    @RequestMapping(value = "/searchUsers")
    public Y9Page<Y9logUserLoginInfo> searchUsers(LogInfoModel loginInfoModel, Y9PageQuery pageQuery) {
        Y9Page<Y9logUserLoginInfo> pageList = userLoginInfoService.searchQuery(Y9LoginUserHolder.getTenantId(),
            String.valueOf(ManagerLevelEnum.GENERAL_USER.getValue()), loginInfoModel, pageQuery.getPage(),
            pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotal(), pageList.getRows(),
            "获取日志分页列表成功");
    }
}
