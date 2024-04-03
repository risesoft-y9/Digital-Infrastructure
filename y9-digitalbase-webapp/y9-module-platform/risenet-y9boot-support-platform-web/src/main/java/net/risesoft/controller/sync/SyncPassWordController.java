package net.risesoft.controller.sync;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

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
public class SyncPassWordController {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9OrganizationService y9OrganizationService;
    private final Y9PersonService y9PersonService;
    private final Y9ManagerService y9ManagerService;

    /**
     * 同步人员信息
     *
     * @param tenantId 租户id
     */
    @RequestMapping("/restPersonPwdByTenantId/{tenantId}")
    @RiseLog(operationName = "修改", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> restAllPersonPwdByTenantId(@PathVariable String tenantId) {
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
     * 同步人员信息
     *
     * @return
     */
    @RequestMapping("/restAllPersonPwd")
    @RiseLog(operationName = "修改", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> restAllPersonPwdInAllTenants() {
        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Y9Organization> y9OrganizationList = y9OrganizationService.list();
            for (Y9Organization organization : y9OrganizationList) {
                List<Y9Person> persons = compositeOrgBaseService.listAllPersonsRecursionDownward(organization.getId());
                for (Y9Person person : persons) {
                    y9PersonService.resetDefaultPassword(person.getId());
                }
            }

            List<Y9Manager> manageList = y9ManagerService.listAll();
            for (Y9Manager manager : manageList) {
                y9ManagerService.resetDefaultPassword(manager.getId());
            }
        }
        return Y9Result.successMsg("同步人员信息完成");
    }

    /**
     * 根据租户id和登陆名称同步人员信息
     *
     * @param tenantId 租户id
     * @param loginName 登录名
     * @return
     */
    @RequestMapping("/restPwd/{tenantId}/{loginName}")
    @RiseLog(operationType = OperationTypeEnum.MODIFY, operationName = "根据租户id和登陆名称，更改个人密码")
    public Y9Result<String> restPwdByTenantIdAndLoginName(@PathVariable String tenantId,
        @PathVariable String loginName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Person person = y9PersonService.getPersonByLoginNameAndTenantId(loginName, tenantId);
        if (person != null) {
            y9PersonService.resetDefaultPassword(person.getId());
        }

        return Y9Result.successMsg("根据租户id和登陆名称同步人员信息完成");
    }
}
