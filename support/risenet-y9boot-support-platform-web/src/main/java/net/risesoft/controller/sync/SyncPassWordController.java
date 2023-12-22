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
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.util.signing.Y9MessageDigest;

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
    private final Y9Properties y9Config;

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
            restPersonPwd(person);
        }

        List<Y9Manager> manageList = y9ManagerService.listAll();
        for (Y9Manager manager : manageList) {
            restManagerPwd(manager);
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
                    restPersonPwd(person);
                }
            }

            List<Y9Manager> manageList = y9ManagerService.listAll();
            for (Y9Manager manager : manageList) {
                restManagerPwd(manager);
            }
        }
        return Y9Result.successMsg("同步人员信息完成");
    }

    private void restManagerPwd(Y9Manager manager) {
        if (manager != null && manager.getId() != null) {
            manager.setPassword(Y9MessageDigest.hashpw(y9Config.getCommon().getDefaultPassword()));
            y9ManagerService.saveOrUpdate(manager);
        }
    }

    private void restPersonPwd(Y9Person person) {
        if (person != null && person.getId() != null) {
            person.setPassword(Y9MessageDigest.hashpw(y9Config.getCommon().getDefaultPassword()));
            y9PersonService.save(person);
        }
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
            restPersonPwd(person);
        }

        return Y9Result.successMsg("根据租户id和登陆名称同步人员信息完成");
    }
}
