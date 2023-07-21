package net.risesoft.api.customgroup;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9CustomGroup;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.relation.Y9CustomGroupMember;
import net.risesoft.model.CustomGroup;
import net.risesoft.model.CustomGroupMember;
import net.risesoft.model.Person;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.org.Y9CustomGroupService;
import net.risesoft.service.relation.Y9CustomGroupMembersService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * 自定义用户组
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@RestController
@RequestMapping(value = "/services/rest/customGroup", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class CustomGroupApiImpl implements CustomGroupApi {

    private final Y9CustomGroupMembersService customGroupMembersService;
    private final Y9CustomGroupService customGroupService;

    /**
     * 添加组成员
     *
     * @param tenantId      租户id
     * @param customGroupId 用户组id
     * @param orgUnitList   组织id列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/addMember")
    public void addMember(@RequestParam String tenantId, @RequestParam String customGroupId, @RequestParam List<String> orgUnitList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        customGroupMembersService.save(orgUnitList, customGroupId);
    }

    /**
     * 删除用户组
     *
     * @param tenantId    租户id
     * @param groupIdList 用户组Id
     * @return boolean 删除是否成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/deleteAllGroup")
    public boolean deleteAllGroup(@RequestParam String tenantId, @RequestParam("groupIds") List<String> groupIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);

        customGroupService.delete(groupIdList);
        return true;
    }

    /**
     * 根据自定义id查找自定义用户组
     *
     * @param tenantId 租户id
     * @param customId 自定义id
     * @return CustomGroup 自定义用户组对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/findCustomGroupByCustomId")
    public CustomGroup findCustomGroupByCustomId(@RequestParam String tenantId, @RequestParam String customId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9CustomGroup y9CustomGroup = customGroupService.findByCustomId(customId);
        return Y9ModelConvertUtil.convert(y9CustomGroup, CustomGroup.class);
    }

    /**
     * 根据id获取用户组
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId  用户组id
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/findCustomGroupById")
    public CustomGroup findCustomGroupById(@RequestParam String tenantId, @RequestParam String personId, @RequestParam String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9CustomGroup customGroup = customGroupService.findById(groupId);
        return Y9ModelConvertUtil.convert(customGroup, CustomGroup.class);
    }

    /**
     * 根据用户组id 解析该用户组下所有人员
     *
     * @param tenantId 租户id
     * @param groupId  用户组id
     * @return List&lt;Person&gt; 人员列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllPersonByGroupId")
    public List<Person> listAllPersonByGroupId(@RequestParam String tenantId, @RequestParam String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9Person> y9PersonList = customGroupMembersService.listAllPersonsByGroupId(groupId);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 根据人员id获取用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @return List&lt;CustomGroup&gt; 用户组对象列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listCustomGroupByUserId")
    public List<CustomGroup> listCustomGroupByUserId(@RequestParam String tenantId, @RequestParam String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9CustomGroup> y9CustomGroupList = customGroupService.listByPersonId(personId);
        return Y9ModelConvertUtil.convert(y9CustomGroupList, CustomGroup.class);
    }

    /**
     * 根据用户组id获取用户组成员
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId  用户组id
     * @return List&lt;CustomGroupMember&gt; 用户组成员列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listCustomGroupMemberByGroupId")
    public List<CustomGroupMember> listCustomGroupMemberByGroupId(@RequestParam String tenantId, @RequestParam String personId, @RequestParam String groupId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9CustomGroupMember> y9CustomGroupMemberList = customGroupMembersService.listByGroupId(groupId);
        return Y9ModelConvertUtil.convert(y9CustomGroupMemberList, CustomGroupMember.class);
    }

    /**
     * 根据用户组id和成员类型，获取用户组员列表
     *
     * @param tenantId   租户id
     * @param personId   人员id
     * @param groupId    用户组id
     * @param memberType 成员类型，如：Person、Position
     * @return List&lt;CustomGroupMember&gt; 用户组员列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listCustomGroupMemberByGroupIdAndMemberType")
    public List<CustomGroupMember> listCustomGroupMemberByGroupIdAndMemberType(@RequestParam String tenantId, @RequestParam String personId, @RequestParam String groupId, @RequestParam String memberType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9CustomGroupMember> y9CustomGroupMemberList = customGroupMembersService.listByGroupIdAndMemberType(groupId, memberType);
        return Y9ModelConvertUtil.convert(y9CustomGroupMemberList, CustomGroupMember.class);
    }

    /**
     * 分页获取自定义用户组
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param page     第几页
     * @param rows     返回多少条数据
     * @return Y9Page&ltMap&lt;String, Object&gt&gt;
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pageCustomGroupByPersonId")
    public Y9Page<CustomGroup> pageCustomGroupByPersonId(@RequestParam String tenantId, @RequestParam String personId, @RequestParam int page, @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Page<Y9CustomGroup> y9CustomGroupPage = customGroupService.pageByPersonId(page, rows, personId);
        return Y9Page.success(page, y9CustomGroupPage.getTotalPages(), y9CustomGroupPage.getTotalElements(), Y9ModelConvertUtil.convert(y9CustomGroupPage.getContent(), CustomGroup.class));
    }

    /**
     * 获取人员分页列表
     *
     * @param tenantId 租户id
     * @param groupId  用户组Id
     * @param page     第几页
     * @param rows     返回多少条数据
     * @return Y9Page&ltMap&lt;String, Object&gt&gt; 人员分页列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pageCustomGroupMemberByGroupId")
    public Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupId(@RequestParam String tenantId, @RequestParam String groupId, @RequestParam int page, @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Page<Y9CustomGroupMember> y9CustomGroupMemberPage = customGroupMembersService.pageByGroupId(groupId, page, rows);
        return Y9Page.success(page, y9CustomGroupMemberPage.getTotalPages(), y9CustomGroupMemberPage.getTotalElements(), Y9ModelConvertUtil.convert(y9CustomGroupMemberPage.getContent(), CustomGroupMember.class));
    }

    /**
     * 根据用户组id和成员类型，获取人员分页列表
     *
     * @param tenantId   租户id
     * @param groupId    用户组Id
     * @param memberType 成员类型，如：Person、Position
     * @param page       第几页
     * @param rows       返回多少条数据
     * @return Map&lt;String, Object&gt; 人员分页列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pageCustomGroupMemberByGroupIdAndMemberType")
    public Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupIdAndMemberType(@RequestParam String tenantId, @RequestParam String groupId, @RequestParam String memberType, @RequestParam int page, @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Page<Y9CustomGroupMember> y9CustomGroupMemberPage = customGroupMembersService.pageByGroupIdAndMemberType(groupId, memberType, page, rows);
        return Y9Page.success(page, y9CustomGroupMemberPage.getTotalPages(), y9CustomGroupMemberPage.getTotalElements(), Y9ModelConvertUtil.convert(y9CustomGroupMemberPage.getContent(), CustomGroupMember.class));
    }

    /**
     * 删除组成员
     *
     * @param tenantId     租户id
     * @param memberIdList id集合
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/removeMembers")
    public boolean removeMembers(@RequestParam String tenantId, @RequestParam("memberIds") List<String> memberIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        customGroupMembersService.delete(memberIdList);
        return true;
    }

    /**
     * 保存用户组
     *
     * @param tenantId    租户id
     * @param customGroup 自定义用户组
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveCustomGroup")
    public CustomGroup saveCustomGroup(@RequestParam String tenantId, CustomGroup customGroup) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9CustomGroup y9CustomGroup = new Y9CustomGroup();
        Y9BeanUtil.copyProperties(customGroup, y9CustomGroup);
        return Y9ModelConvertUtil.convert(customGroupService.save(y9CustomGroup), CustomGroup.class);
    }

    /**
     * 保存用户组排序
     *
     * @param tenantId   租户id
     * @param sortIdList 排序后的用户组id集合
     * @return boolean 是否保存排序成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveCustomGroupOrder")
    public boolean saveCustomGroupOrder(@RequestParam String tenantId, @RequestParam("sortIds") List<String> sortIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return customGroupService.saveCustomGroupOrder(sortIdList);
    }

    /**
     * 保存成员排序
     *
     * @param tenantId     租户id
     * @param memberIdList 排序后的id集合
     * @return boolean 是否保存排序成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveMemberOrder")
    public boolean saveMemberOrder(@RequestParam String tenantId, @RequestParam("memberIds") List<String> memberIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return customGroupMembersService.saveOrder(memberIdList);
    }

    /**
     * 保存或者更新用户组
     *
     * @param tenantId     租户id
     * @param personId     人员Id
     * @param personIdList 人员id集合
     * @param groupId      用户组Id
     * @param groupName    用户组名称
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveOrUpdateCustomGroup")
    public CustomGroup saveOrUpdateCustomGroup(@RequestParam String tenantId, @RequestParam String personId, @RequestParam("personIds") List<String> personIdList, @RequestParam String groupId, @RequestParam String groupName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9CustomGroup customGroup = customGroupService.saveOrUpdate(personId, personIdList, groupId, groupName);
        return Y9ModelConvertUtil.convert(customGroup, CustomGroup.class);
    }

    /**
     * 共享用户组
     *
     * @param tenantId     租户id
     * @param personIdList 人员id集合
     * @param groupIdList  用户组id集合
     * @return boolean 分享是否成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/shareCustomGroup")
    public boolean shareCustomGroup(@RequestParam String tenantId, @RequestParam("personIds") List<String> personIdList, @RequestParam("groupIds") List<String> groupIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return customGroupService.share(personIdList, groupIdList);
    }

}
