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
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.platform.ResourceTypeEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.init.InitTenantDataService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.service.resource.CompositeResourceService;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9DataCatalogService;
import net.risesoft.y9public.service.resource.Y9MenuService;
import net.risesoft.y9public.service.resource.Y9OperationService;

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
public class SyncController {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9OrganizationService y9OrganizationService;
    private final Y9PersonService y9PersonService;
    private final Y9PositionService y9PositionService;
    private final Y9ManagerService y9ManagerService;

    private final InitTenantDataService initTenantDataService;

    private final CompositeResourceService compositeResourceService;
    private final Y9AppService y9AppService;
    private final Y9MenuService y9MenuService;
    private final Y9OperationService y9OperationService;
    private final Y9DataCatalogService y9DataCatalogService;

    /**
     * 初始化租户三员
     *
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/initManagers")
    @RiseLog(operationName = "初始化租户三员", operationType = OperationTypeEnum.ADD)
    public Y9Result<String> initManagers() {
        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9LoginUserHolder.setTenantId(tenantId);
            LOGGER.debug("初始化租户[{}]三员", tenantId);
            initTenantDataService.initManagers();
        }
        return Y9Result.successMsg("初始化租户三员完成");
    }

    /**
     * 初始化数据字典
     *
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/initOptionClass")
    @RiseLog(operationName = "初始化数据字典", operationType = OperationTypeEnum.ADD)
    public Y9Result<String> initOptionClass() {
        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9LoginUserHolder.setTenantId(tenantId);
            LOGGER.debug("初始化租户[{}]数据字典", tenantId);
            initTenantDataService.initOptionClass();
        }
        return Y9Result.successMsg("初始化数据字典成功");
    }

    /**
     * 同步人员信息
     *
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/personInfo")
    @RiseLog(operationName = "同步人员信息", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncAllPersonInAllTenants() {
        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9LoginUserHolder.setTenantId(tenantId);
            LOGGER.debug("同步租户[{}]人员信息", tenantId);
            List<Y9Organization> y9OrganizationList = y9OrganizationService.list();
            for (Y9Organization organization : y9OrganizationList) {
                List<Y9Person> persons = compositeOrgBaseService.listAllDescendantPersons(organization.getId());
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
     * 根据租户id，同步人员信息
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/personInfo/{tenantId}")
    @RiseLog(operationName = "同步人员信息", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncPersonByTenantId(@PathVariable String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9Person> persons = y9PersonService.list(null);
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
     * @return {@code Y9Result<String>}
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
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/positionInfo")
    @RiseLog(operationName = "同步岗位信息", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncPosition() {
        List<String> tenantIdList = Y9PlatformUtil.getTenantIds();
        for (String tenantId : tenantIdList) {
            Y9LoginUserHolder.setTenantId(tenantId);
            LOGGER.debug("同步租户[{}]岗位信息", tenantId);
            List<Y9Position> positions = y9PositionService.listAll();
            for (Y9Position position : positions) {
                position.setTenantId(tenantId);
                y9PositionService.saveOrUpdate(position);
            }
        }
        return Y9Result.successMsg("同步岗位信息完成");
    }

    /**
     * 同步租户岗位信息
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/positionInfo/{tenantId}")
    @RiseLog(operationName = "同步岗位信息", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncPositionByTenantId(@PathVariable String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9Position> positions = y9PositionService.listAll();
        for (Y9Position position : positions) {
            position.setTenantId(tenantId);
            y9PositionService.saveOrUpdate(position);
        }
        return Y9Result.successMsg("同步岗位信息完成");
    }

    @RequestMapping("/resourceGuidPath")
    @RiseLog(operationName = "同步资源的 guidPath", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncResourceGuidPath() {
        List<Y9App> y9Apps = compositeResourceService.listRootResourceList();
        for (Y9App y9App : y9Apps) {
            y9AppService.saveOrUpdate(y9App);
            recursiveUpdate(y9App.getId());
        }
        List<Y9DataCatalog> y9DataCatalogs = y9DataCatalogService.listRoot();
        for (Y9DataCatalog y9DataCatalog : y9DataCatalogs) {
            y9DataCatalogService.saveOrUpdate(y9DataCatalog);
            recursiveUpdate(y9DataCatalog.getId());
        }
        return Y9Result.successMsg("同步资源的 guidPath 完成");
    }

    private void recursiveUpdate(String parentId) {
        List<Y9ResourceBase> y9ResourceBases = compositeResourceService.listByParentId(parentId);
        for (Y9ResourceBase y9ResourceBase : y9ResourceBases) {
            if (ResourceTypeEnum.MENU.equals(y9ResourceBase.getResourceType())) {
                y9MenuService.saveOrUpdate((Y9Menu)y9ResourceBase);
            } else if (ResourceTypeEnum.OPERATION.equals(y9ResourceBase.getResourceType())) {
                y9OperationService.saveOrUpdate((Y9Operation)y9ResourceBase);
            } else if (ResourceTypeEnum.DATA_CATALOG.equals(y9ResourceBase.getResourceType())) {
                y9DataCatalogService.saveOrUpdate((Y9DataCatalog)y9ResourceBase);
            }
            recursiveUpdate(y9ResourceBase.getId());
        }
    }

}
