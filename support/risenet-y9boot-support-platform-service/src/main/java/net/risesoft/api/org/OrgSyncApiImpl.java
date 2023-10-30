package net.risesoft.api.org;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.model.Department;
import net.risesoft.model.Group;
import net.risesoft.model.MessageOrg;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Organization;
import net.risesoft.model.Person;
import net.risesoft.model.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.event.Y9PublishedEvent;
import net.risesoft.y9public.entity.event.Y9PublishedEventSyncHistory;
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
    private final Y9DepartmentService orgDepartmentService;
    private final Y9GroupService orgGroupService;
    private final Y9OrganizationService orgOrganizationService;
    private final Y9PersonService orgPersonService;
    private final Y9PositionService orgPositionService;
    private final Y9PublishedEventService y9PublishedEventService;
    private final Y9PublishedEventSyncHistoryService y9PublishedEventSyncHistoryService;

    private OrgUnit getOrgBase(String eventType, String objId) {
        if (StringUtils.isBlank(objId)) {
            return null;
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_ORGANIZATION.equals(eventType)) {
            Y9Organization org = orgOrganizationService.getById(objId);
            return Y9ModelConvertUtil.convert(org, Organization.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_DEPARTMENT.equals(eventType)) {
            Y9Department dept = orgDepartmentService.getById(objId);
            return Y9ModelConvertUtil.convert(dept, Department.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_PERSON.equals(eventType)) {
            Y9Person person = orgPersonService.getById(objId);
            return Y9ModelConvertUtil.convert(person, Person.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_ORGANIZATION.equals(eventType)) {
            Y9Organization org = orgOrganizationService.getById(objId);
            return Y9ModelConvertUtil.convert(org, Organization.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_DEPARTMENT.equals(eventType)) {
            Y9Department dept = orgDepartmentService.getById(objId);
            return Y9ModelConvertUtil.convert(dept, Department.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_PERSON.equals(eventType)) {
            Y9Person person = orgPersonService.getById(objId);
            return Y9ModelConvertUtil.convert(person, Person.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_ORGANIZATION.equals(eventType)) {
            Y9Organization org = orgOrganizationService.getById(objId);
            return Y9ModelConvertUtil.convert(org, Organization.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_DEPARTMENT.equals(eventType)) {
            Y9Department dept = orgDepartmentService.getById(objId);
            return Y9ModelConvertUtil.convert(dept, Department.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_PERSON.equals(eventType)) {
            Y9Person person = orgPersonService.getById(objId);
            return Y9ModelConvertUtil.convert(person, Person.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_GROUP.equals(eventType)) {
            Y9Group group = orgGroupService.getById(objId);
            return Y9ModelConvertUtil.convert(group, Group.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_POSITION.equals(eventType)) {
            Y9Position position = orgPositionService.getById(objId);
            return Y9ModelConvertUtil.convert(position, Position.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_GROUP.equals(eventType)) {
            Y9Group group = orgGroupService.getById(objId);
            return Y9ModelConvertUtil.convert(group, Group.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_POSITION.equals(eventType)) {
            Y9Position position = orgPositionService.getById(objId);
            return Y9ModelConvertUtil.convert(position, Position.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_GROUP.equals(eventType)) {
            Y9Group group = orgGroupService.getById(objId);
            return Y9ModelConvertUtil.convert(group, Group.class);
        }
        if (Y9OrgEventConst.RISEORGEVENT_TYPE_DELETE_POSITION.equals(eventType)) {
            Y9Position position = orgPositionService.getById(objId);
            return Y9ModelConvertUtil.convert(position, Position.class);
        }
        return null;
    }

    /**
     * 根据机构id，全量获取整个组织机构数据
     *
     * @param appName 应用名称
     * @param tenantId 租户id
     * @param organizationId 机构id
     * @return Y9Result&lt;MessageOrg&gt; 整个组织机构对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<MessageOrg> fullSync(@RequestParam("appName") @NotBlank String appName,
        @RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Date syncTime = new Date();
        HashMap<String, Serializable> dateMap =
            compositeOrgBaseService.getSyncMap(organizationId, OrgTypeEnum.ORGANIZATION.getEnName(), 1);
        MessageOrg event =
            new MessageOrg(dateMap, Y9OrgEventConst.RISEORGEVENT_TYPE_SYNC, Y9LoginUserHolder.getTenantId());
        y9PublishedEventSyncHistoryService.saveOrUpdate(tenantId, appName, syncTime);
        return Y9Result.success(event, "获取成功！");
    }

    /**
     * 增量获取组织操作列表 系统记录了上一次同步的时间，从上一次同步时间往后获取数据
     *
     * @param appName 应用名称
     * @param tenantId 租户id
     * @return Y9Result&lt;List&lt;MessageOrg&gt;&gt; 事件列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<MessageOrg>> incrSync(@RequestParam("appName") @NotBlank String appName,
        @RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Date syncTime = new Date();
        Date startTime = null;
        Optional<Y9PublishedEventSyncHistory> y9PublishedEventSyncHistoryOptional =
            y9PublishedEventSyncHistoryService.findByTenantIdAndAppName(tenantId, appName);
        if (y9PublishedEventSyncHistoryOptional.isPresent()) {
            startTime = y9PublishedEventSyncHistoryOptional.get().getLastSyncTime();
        }

        List<Y9PublishedEvent> list = y9PublishedEventService.listByTenantId(tenantId, startTime);
        List<MessageOrg> eventList = new ArrayList<>();
        for (Y9PublishedEvent event : list) {
            if (StringUtils.isBlank(event.getEntityJson())) {
                OrgUnit org = getOrgBase(event.getEventType(), event.getObjId());
                MessageOrg riseEvent =
                    new MessageOrg(Y9JsonUtil.writeValueAsString(org), event.getEventType(), tenantId);
                eventList.add(riseEvent);
            } else {
                MessageOrg riseEvent = new MessageOrg(event.getEntityJson(), event.getEventType(), tenantId);
                eventList.add(riseEvent);
            }
        }
        y9PublishedEventSyncHistoryService.saveOrUpdate(tenantId, appName, syncTime);
        return Y9Result.success(eventList, "获取成功！");
    }

}
