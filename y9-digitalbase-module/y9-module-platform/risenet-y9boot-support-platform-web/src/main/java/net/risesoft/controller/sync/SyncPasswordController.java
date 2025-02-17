package net.risesoft.controller.sync;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Manager;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 同步信息
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/sync", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class SyncPasswordController {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9OrganizationService y9OrganizationService;
    private final Y9PersonService y9PersonService;
    private final Y9ManagerService y9ManagerService;

    /**
     * 同步人员信息
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/resetPersonPwd/{tenantId}")
    @RiseLog(operationName = "修改", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> resetAllPersonPwdByTenantId(@PathVariable String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9Person> persons = y9PersonService.listAll();
        for (Y9Person person : persons) {
            y9PersonService.resetDefaultPassword(person.getId());
        }

        List<Y9Manager> manageList = y9ManagerService.listAll();
        for (Y9Manager manager : manageList) {
            y9ManagerService.resetDefaultPassword(manager.getId());
        }

        return Y9Result.successMsg("同步人员信息完成");
    }

    /**
     * 重置所有租户人员密码
     *
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/resetAllPersonPwd")
    @RiseLog(operationName = "修改", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> resetAllPersonPwdInAllTenants() {
        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9LoginUserHolder.setTenantId(tenantId);
            LOGGER.debug("同步租户[{}]人员密码", tenantId);
            List<Y9Organization> y9OrganizationList = y9OrganizationService.list();
            for (Y9Organization organization : y9OrganizationList) {
                List<Y9Person> persons = compositeOrgBaseService.listAllDescendantPersons(organization.getId());
                for (Y9Person person : persons) {
                    y9PersonService.resetDefaultPassword(person.getId());
                }
            }

            List<Y9Manager> manageList = y9ManagerService.listAll();
            for (Y9Manager manager : manageList) {
                y9ManagerService.resetDefaultPassword(manager.getId());
            }
        }
        return Y9Result.successMsg("重置所有租户人员密码完成");
    }

    /**
     * 根据租户id和登录名称重置人员密码
     *
     * @param tenantId 租户id
     * @param loginName 登录名
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/resetPwd/{tenantId}/{loginName}")
    @RiseLog(operationType = OperationTypeEnum.MODIFY, operationName = "根据租户id和登陆名称，更改个人密码")
    public Y9Result<String> resetPwdByTenantIdAndLoginName(@PathVariable String tenantId,
        @PathVariable String loginName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Person person = y9PersonService.getPersonByLoginNameAndTenantId(loginName, tenantId);
        if (person != null) {
            y9PersonService.resetDefaultPassword(person.getId());
        }

        return Y9Result.successMsg("根据租户id和登录名称重置人员密码完成");
    }

    /**
     * 重置租户管理员密码
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/resetAllManagerPwdByTenantId/{tenantId}")
    @RiseLog(operationName = "修改", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> resetAllManagerPwdByTenantId(@PathVariable String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9Manager> manageList = y9ManagerService.listAll();
        for (Y9Manager manager : manageList) {
            y9ManagerService.resetDefaultPassword(manager.getId());
        }

        return Y9Result.successMsg("同步管理员信息完成");
    }

    /**
     * 重置所有租户管理员密码
     *
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/resetAllManagerPwd")
    @RiseLog(operationName = "修改", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> resetAllManagerPwdInAllTenants() {
        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9LoginUserHolder.setTenantId(tenantId);
            LOGGER.debug("同步租户管理员[{}]人员密码", tenantId);
            List<Y9Manager> manageList = y9ManagerService.listAll();
            for (Y9Manager manager : manageList) {
                y9ManagerService.resetDefaultPassword(manager.getId());
            }
        }
        return Y9Result.successMsg("重置所有租户管理员密码完成");
    }
}
