package net.risesoft.api.org;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.model.Group;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.relation.Y9PersonsToGroupsService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * 用户组服务组件
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
@RequestMapping(value = "/services/rest/group", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class GroupApiImpl implements GroupApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9PersonsToGroupsService y9PersonsToGroupsService;
    private final Y9GroupService y9GroupService;
    private final Y9PersonService y9PersonService;

    /**
     * 用户组添加人员
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @param personId 人员ID
     * @return boolean 是否添加成功
     * @since 9.6.0
     */
    @Override
    public boolean addPerson2Group(@RequestParam String tenantId, @RequestParam String groupId, @RequestParam String personId) {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(groupId) || StringUtils.isBlank(personId)) {
            return false;
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        
        if (y9GroupService.existsById(groupId) && y9PersonService.existsById(personId)) {
            y9PersonsToGroupsService.addPersons(groupId, new String[] {personId});
            return true;
        }
        return false;
    }

    /**
     * 创建用户组
     *
     * @param tenantId 租户id
     * @param groupJson 用户组对象
     * @return Group 用户组对象
     * @since 9.6.0
     */
    @Override
    public Group createGroup(@RequestParam String tenantId, @RequestParam String groupJson) {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(groupJson)) {
            return null;
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9Group y9Group = Y9JsonUtil.readValue(groupJson, Y9Group.class);
        y9Group = y9GroupService.createGroup(y9Group, compositeOrgBaseService.getOrgBase(y9Group.getParentId()));
        return Y9ModelConvertUtil.convert(y9Group, Group.class);
    }

    /**
     * 删除用户组
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @return true 删除成功，false 删除失败
     * @since 9.6.0
     */
    @Override
    public boolean deleteGroup(@RequestParam String tenantId, @RequestParam String groupId) {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(groupId)) {
            return false;
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        
        y9GroupService.delete(groupId);
        return true;
    }

    /**
     * 根据id获取用户组对象
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return Group 用户组对象
     * @since 9.6.0
     */
    @Override
    public Group getGroup(@RequestParam @NotBlank String tenantId, @RequestParam @NotBlank String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9Group y9Group = y9GroupService.findById(groupId);
        return Y9ModelConvertUtil.convert(y9Group, Group.class);
    }

    /**
     * 获取用户组父节点
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return OrgUnit 组织架构节点对象
     * @since 9.6.0
     */
    @Override
    public OrgUnit getParent(@RequestParam String tenantId, @RequestParam String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9OrgBase parent = compositeOrgBaseService.getParent(groupId);
        return ModelConvertUtil.orgBaseToOrgUnit(parent);
    }

    /**
     * 根据租户id和路径获取所有用户组
     *
     * @param tenantId 租户id
     * @param dn 路径
     * @return List<Group> 用户组对象集合
     * @since 9.6.0
     */
    @Override
    public List<Group> listByDn(@RequestParam String tenantId, @RequestParam String dn) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9Group> y9GroupList = y9GroupService.listByDn(dn);
        return Y9ModelConvertUtil.convert(y9GroupList, Group.class);
    }

    /**
     * 获取组内的人员列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return List<Person> 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listPersons(@RequestParam String tenantId, @RequestParam String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9Person> y9PersonList = y9PersonService.listByGroupId(groupId);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 从用户组移除人员
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @param personId 人员ID
     * @return boolean 是否移除成功
     * @since 9.6.0
     */
    @Override
    public boolean removePerson(@RequestParam String tenantId, @RequestParam String groupId, @RequestParam String personId) {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(groupId) || StringUtils.isBlank(personId)) {
            return false;
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        
        y9PersonsToGroupsService.removePersons(groupId, new String[] {personId});
        return true;
    }

    /**
     * 更新用户组
     *
     * @param tenantId 租户id
     * @param groupJson 用户组对象JSON字符串
     * @return Group 用户组对象
     * @since 9.6.0
     */
    @Override
    public Group updateGroup(@RequestParam String tenantId, @RequestParam String groupJson) {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(groupJson)) {
            return null;
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9Group y9Group = Y9JsonUtil.readValue(groupJson, Y9Group.class);
        y9Group = y9GroupService.saveOrUpdate(y9Group, compositeOrgBaseService.getOrgBase(y9Group.getParentId()));
        return Y9ModelConvertUtil.convert(y9Group, Group.class);
    }

}
