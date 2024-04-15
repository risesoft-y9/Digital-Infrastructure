package net.risesoft.api.org;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgSyncApi;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Group;
import net.risesoft.model.platform.MessageOrg;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.model.platform.SyncOrgUnits;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventTypeConst;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.event.Y9OrgSyncRole;
import net.risesoft.y9public.entity.event.Y9PublishedEvent;
import net.risesoft.y9public.entity.event.Y9PublishedEventSyncHistory;
import net.risesoft.y9public.repository.event.Y9OrgSyncRoleRepository;
import net.risesoft.y9public.service.event.Y9PublishedEventService;
import net.risesoft.y9public.service.event.Y9PublishedEventSyncHistoryService;

/**
 * 组织同步组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/orgSync", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrgSyncApiImpl implements OrgSyncApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9DepartmentService y9DepartmentService;
    private final Y9GroupService y9GroupService;
    private final Y9OrganizationService y9OrganizationService;
    private final Y9PersonService y9PersonService;
    private final Y9PositionService y9PositionService;
    private final Y9PublishedEventService y9PublishedEventService;
    private final Y9PublishedEventSyncHistoryService y9PublishedEventSyncHistoryService;
    private final Y9OrgSyncRoleRepository y9OrgSyncRoleRepository;

    /**
     * 根据机构id，全量获取整个组织机构所有组织节点数据
     *
     * @param appName 应用名称
     * @param tenantId 租户id
     * @param organizationId 机构id
     * @return {@code Y9Result<MessageOrg>} 通用请求返回对象 - data 是整个组织机构所有组织节点集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<MessageOrg<SyncOrgUnits>> fullSync(@RequestParam("appName") @NotBlank String appName,
        @RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9OrgSyncRole role = y9OrgSyncRoleRepository.findById(appName).orElse(null);
        if (role == null) {
            return Y9Result.success(null, "未拥有获取组织节点数据的权限");
        }
        Date syncTime = new Date();
        SyncOrgUnits syncOrgUnits =
            compositeOrgBaseService.getSyncOrgUnits(organizationId, OrgTypeEnum.ORGANIZATION, Boolean.TRUE);
        MessageOrg<SyncOrgUnits> event =
            new MessageOrg<>(syncOrgUnits, Y9OrgEventTypeConst.SYNC, Y9LoginUserHolder.getTenantId());
        y9PublishedEventSyncHistoryService.saveOrUpdate(tenantId, appName, syncTime, 1);
        return Y9Result.success(event, "获取成功！");
    }

    @Override
    public Y9Page<Department> fullSyncDept(@RequestParam String appName, @RequestParam String tenantId,
        @RequestParam int page, @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9OrgSyncRole role = y9OrgSyncRoleRepository.findById(appName).orElse(null);
        if (role == null) {
            return Y9Page.success(0, 0, 0, null, "应用名称不存在");
        }
        Date syncTime = new Date();
        Page<Y9Department> deptPage = compositeOrgBaseService.deptPage(role.getOrgIds(), page, rows);
        List<Department> list = Y9ModelConvertUtil.convert(deptPage.getContent(), Department.class);
        y9PublishedEventSyncHistoryService.saveOrUpdate(tenantId, appName, syncTime, 1);
        return Y9Page.success(deptPage.getNumber(), deptPage.getTotalPages(), deptPage.getTotalElements(), list,
            "获取成功");
    }

    @Override
    public Y9Page<Person> fullSyncUser(@RequestParam String appName, @RequestParam String tenantId,
        @RequestParam String type, @RequestParam int page, @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9OrgSyncRole role = y9OrgSyncRoleRepository.findById(appName).orElse(null);
        if (role == null) {
            return Y9Page.success(0, 0, 0, null, "应用名称不存在");
        }
        Date syncTime = new Date();
        Page<Y9Person> personPage = compositeOrgBaseService.personPage(role.getOrgIds(), type, page, rows);
        List<Person> list = Y9ModelConvertUtil.convert(personPage.getContent(), Person.class);
        y9PublishedEventSyncHistoryService.saveOrUpdate(tenantId, appName, syncTime, 1);
        return Y9Page.success(personPage.getNumber(), personPage.getTotalPages(), personPage.getTotalElements(), list,
            "获取成功");
    }

    private OrgUnit getOrgBase(String eventType, String objId) {
        if (Y9OrgEventTypeConst.ORGANIZATION_ADD.equals(eventType)) {
            Y9Organization org = y9OrganizationService.getById(objId);
            return Y9ModelConvertUtil.convert(org, Organization.class);
        }
        if (Y9OrgEventTypeConst.DEPARTMENT_ADD.equals(eventType)) {
            Y9Department dept = y9DepartmentService.getById(objId);
            return Y9ModelConvertUtil.convert(dept, Department.class);
        }
        if (Y9OrgEventTypeConst.PERSON_ADD.equals(eventType)) {
            Y9Person person = y9PersonService.getById(objId);
            return Y9ModelConvertUtil.convert(person, Person.class);
        }
        if (Y9OrgEventTypeConst.ORGANIZATION_UPDATE.equals(eventType)) {
            Y9Organization org = y9OrganizationService.getById(objId);
            return Y9ModelConvertUtil.convert(org, Organization.class);
        }
        if (Y9OrgEventTypeConst.DEPARTMENT_UPDATE.equals(eventType)) {
            Y9Department dept = y9DepartmentService.getById(objId);
            return Y9ModelConvertUtil.convert(dept, Department.class);
        }
        if (Y9OrgEventTypeConst.PERSON_UPDATE.equals(eventType)) {
            Y9Person person = y9PersonService.getById(objId);
            return Y9ModelConvertUtil.convert(person, Person.class);
        }
        if (Y9OrgEventTypeConst.ORGANIZATION_DELETE.equals(eventType)) {
            Y9Organization org = y9OrganizationService.getById(objId);
            return Y9ModelConvertUtil.convert(org, Organization.class);
        }
        if (Y9OrgEventTypeConst.DEPARTMENT_DELETE.equals(eventType)) {
            Y9Department dept = y9DepartmentService.getById(objId);
            return Y9ModelConvertUtil.convert(dept, Department.class);
        }
        if (Y9OrgEventTypeConst.PERSON_DELETE.equals(eventType)) {
            Y9Person person = y9PersonService.getById(objId);
            return Y9ModelConvertUtil.convert(person, Person.class);
        }
        if (Y9OrgEventTypeConst.GROUP_ADD.equals(eventType)) {
            Y9Group group = y9GroupService.getById(objId);
            return Y9ModelConvertUtil.convert(group, Group.class);
        }
        if (Y9OrgEventTypeConst.POSITION_ADD.equals(eventType)) {
            Y9Position position = y9PositionService.getById(objId);
            return Y9ModelConvertUtil.convert(position, Position.class);
        }
        if (Y9OrgEventTypeConst.GROUP_UPDATE.equals(eventType)) {
            Y9Group group = y9GroupService.getById(objId);
            return Y9ModelConvertUtil.convert(group, Group.class);
        }
        if (Y9OrgEventTypeConst.POSITION_UPDATE.equals(eventType)) {
            Y9Position position = y9PositionService.getById(objId);
            return Y9ModelConvertUtil.convert(position, Position.class);
        }
        if (Y9OrgEventTypeConst.GROUP_DELETE.equals(eventType)) {
            Y9Group group = y9GroupService.getById(objId);
            return Y9ModelConvertUtil.convert(group, Group.class);
        }
        if (Y9OrgEventTypeConst.POSITION_DELETE.equals(eventType)) {
            Y9Position position = y9PositionService.getById(objId);
            return Y9ModelConvertUtil.convert(position, Position.class);
        }
        return null;
    }

    /**
     * 增量获取组织操作列表 系统记录了上一次同步的时间，从上一次同步时间往后获取数据
     *
     * @param appName 应用名称
     * @param tenantId 租户id
     * @return {@code Y9Result<List<MessageOrg>>} 通用请求返回对象 - data 是事件列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<MessageOrg<OrgUnit>>> incrSync(@RequestParam("appName") @NotBlank String appName,
        @RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9OrgSyncRole role = y9OrgSyncRoleRepository.findById(appName).orElse(null);
        if (role == null) {
            return Y9Result.success(null, "应用名称不存在");
        }
        Date syncTime = new Date();
        Date startTime = null;
        Optional<Y9PublishedEventSyncHistory> y9PublishedEventSyncHistoryOptional =
            y9PublishedEventSyncHistoryService.findByTenantIdAndAppName(tenantId, appName);
        if (y9PublishedEventSyncHistoryOptional.isPresent()) {
            startTime = y9PublishedEventSyncHistoryOptional.get().getLastSyncTime();
        }

        List<Y9PublishedEvent> list = y9PublishedEventService.listByTenantId(tenantId, startTime);
        List<MessageOrg<OrgUnit>> eventList = new ArrayList<>();
        for (Y9PublishedEvent event : list) {
            if (StringUtils.isBlank(event.getEntityJson())) {
                OrgUnit org = getOrgBase(event.getEventType(), event.getObjId());
                MessageOrg<OrgUnit> riseEvent = new MessageOrg<>(org, event.getEventType(), tenantId);
                eventList.add(riseEvent);
            } else {
                boolean isAdd = false;
                OrgUnit orgUnit = Y9JsonUtil.readValue(event.getEntityJson(), OrgUnit.class);
                String guidPath = orgUnit.getGuidPath();
                if (StringUtils.isNotBlank(guidPath)) {
                    String[] ids = role.getOrgIds().split(",");
                    for (String id : ids) {
                        if (guidPath.contains(id)) {
                            isAdd = true;
                            break;
                        }
                    }
                } else {// 岗位操作信息没有guidPath字段，默认通过
                    isAdd = true;
                }
                if (isAdd) {
                    MessageOrg<OrgUnit> riseEvent = new MessageOrg<>(orgUnit, event.getEventType(), tenantId);
                    eventList.add(riseEvent);
                }
            }
        }
        y9PublishedEventSyncHistoryService.saveOrUpdate(tenantId, appName, syncTime, 0);
        return Y9Result.success(eventList, "获取成功！");
    }

    @Override
    public Y9Result<String> syncTime(@RequestParam String appName, @RequestParam String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Date syncTime = null;
        Optional<Y9PublishedEventSyncHistory> history =
            y9PublishedEventSyncHistoryService.findByTenantIdAndAppName(tenantId, appName);
        if (history.isPresent()) {
            syncTime = history.get().getSinceSyncTime();
        } else {
            return Y9Result.failure("获取不到记录！");
        }
        y9PublishedEventSyncHistoryService.saveOrUpdate(tenantId, appName, syncTime, 1);
        return Y9Result.success("操作成功！");
    }

}
