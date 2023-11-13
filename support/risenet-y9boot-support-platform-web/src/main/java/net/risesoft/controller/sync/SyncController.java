package net.risesoft.controller.sync;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Manager;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.platform.TenantTypeEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.dictionary.Y9OptionClassService;
import net.risesoft.service.init.InitTenantDataService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.tenant.Y9Tenant;

/**
 * 同步信息
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/sync", produces = "application/json")
@RequiredArgsConstructor
public class SyncController {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9OrganizationService y9OrganizationService;
    private final Y9PersonService y9PersonService;
    private final Y9PositionService y9PositionService;
    private final Y9ManagerService y9ManagerService;
    private final Y9OptionClassService y9OptionClassService;
    private final InitTenantDataService initTenantDataService;

    /**
     * 初始化租户三员
     *
     * @return
     */
    @RequestMapping("/initManagers")
    @RiseLog(operationName = "初始化租户三员", operationType = OperationTypeEnum.ADD)
    public Y9Result<String> initManagers() {
        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9Tenant tenant = Y9PlatformUtil.getTenantById(tenantId);
            if (tenant.getTenantType().equals(TenantTypeEnum.TENANT)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                initTenantDataService.initManagers();
            }
        }
        return Y9Result.successMsg("初始化租户三员完成");
    }

    /**
     * 初始化数据字典
     *
     * @return
     */
    @RequestMapping("/initOptionClass")
    @RiseLog(operationName = "初始化数据字典", operationType = OperationTypeEnum.ADD)
    public Y9Result<String> initOptionClass() {
        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9Tenant tenant = Y9PlatformUtil.getTenantById(tenantId);
            if (TenantTypeEnum.TENANT.equals(tenant.getTenantType())) {
                Y9LoginUserHolder.setTenantId(tenantId);
                initTenantDataService.initOptionClass();
            }
        }
        return Y9Result.successMsg("初始化数据字典成功");
    }

    /**
     * 同步人员信息
     *
     * @return
     */
    @RequestMapping("/personInfo")
    @RiseLog(operationName = "同步人员信息", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncAllPersonInAllTenants() {
        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Y9Organization> y9OrganizationList = y9OrganizationService.list();
            for (Y9Organization organization : y9OrganizationList) {
                List<Y9Person> persons = compositeOrgBaseService.listAllPersonsRecursionDownward(organization.getId());
                for (Y9Person person : persons) {
                    if (person != null && person.getId() != null) {
                        y9PersonService.saveOrUpdate(person, null);
                    }
                }
            }
            List<Y9Manager> managerList = y9ManagerService.listAll();
            for (Y9Manager manager : managerList) {
                y9ManagerService.saveOrUpdate(manager);

            }
        }
        return Y9Result.successMsg("同步人员信息完成");
    }

    /**
     * 同步人员信息
     *
     * @param tenantId 租户id
     */
    @RequestMapping("/personInfo/{tenantId}")
    @RiseLog(operationName = "同步人员信息", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncPersonByTenantId(@PathVariable String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9Person> persons = y9PersonService.list();
        for (Y9Person person : persons) {
            if (person != null && person.getId() != null) {
                y9PersonService.saveOrUpdate(person, null);

            }
        }
        List<Y9Manager> managerList = y9ManagerService.listAll();
        for (Y9Manager manager : managerList) {
            y9ManagerService.saveOrUpdate(manager);

        }
        return Y9Result.successMsg("同步人员信息完成");
    }

    /**
     * 根据租户id和登录名称同步人员信息
     *
     * @param tenantId 租户id
     * @param loginName 登录名
     * @return
     */
    @RequestMapping("/personInfo/{tenantId}/{loginName}")
    @RiseLog(operationName = "根据租户id和登录名称同步人员信息", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncPersonByTenantIdAndLoginName(@PathVariable String tenantId,
        @PathVariable String loginName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Person person = y9PersonService.getPersonByLoginNameAndTenantId(loginName, tenantId);
        if (person != null && person.getId() != null) {
            y9PersonService.saveOrUpdate(person, null);
        }
        return Y9Result.successMsg("根据租户id和登录名称同步人员信息完成");
    }

    /**
     * 同步岗位信息
     *
     * @return
     */
    @RequestMapping("/positionInfo")
    @RiseLog(operationName = "同步岗位信息", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncPosition() {
        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Y9Position> positions = y9PositionService.listAll();
            for (Y9Position position : positions) {
                y9PositionService.saveOrUpdate(position);
            }
        }

        return Y9Result.successMsg("同步岗位信息完成");
    }
}
