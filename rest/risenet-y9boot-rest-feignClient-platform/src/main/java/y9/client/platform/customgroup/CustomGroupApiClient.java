package y9.client.platform.customgroup;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.customgroup.CustomGroupApi;
import net.risesoft.model.CustomGroup;
import net.risesoft.model.CustomGroupMember;
import net.risesoft.model.Person;
import net.risesoft.pojo.Y9Page;

/**
 * 自定义用户组
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "CustomGroupApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/customGroup")
public interface CustomGroupApiClient extends CustomGroupApi {

    /**
     * 添加组成员
     *
     * @param tenantId 租户id
     * @param customGroupId 用户组id
     * @param orgUnitList 组织id列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/addMember")
    void addMember(@RequestParam("tenantId") String tenantId, @RequestParam("customGroupId") String customGroupId, @RequestParam("orgUnitList") List<String> orgUnitList);

    /**
     * 删除用户组
     *
     * @param tenantId 租户id
     * @param groupIds 用户组Ids,多个“,”隔开
     * @return boolean 删除是否成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/deleteAllGroup")
    boolean deleteAllGroup(@RequestParam("tenantId") String tenantId, @RequestParam("groupIds") List<String> groupIds);

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
    CustomGroup findCustomGroupByCustomId(@RequestParam("tenantId") String tenantId, @RequestParam("customId") String customId);

    /**
     * 根据id获取用户组
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/findCustomGroupById")
    CustomGroup findCustomGroupById(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("groupId") String groupId);

    /**
     * 根据用户组id 解析该用户组下所有人员
     *
     * @param tenantId 租户id
     * @param groupId 用户组id
     * @return List&lt;Person&gt; 人员列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllPersonByGroupId")
    List<Person> listAllPersonByGroupId(@RequestParam("tenantId") String tenantId, @RequestParam("groupId") String groupId);

    /**
     * 根据人员id获取用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @return List&lt;CustomGroup&gt;
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listCustomGroupByUserId")
    List<CustomGroup> listCustomGroupByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId);

    /**
     * 根据用户组id获取用户组成员
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param groupId 用户组id
     * @return List&lt;CustomGroupMember&gt; 用户组成员列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listCustomGroupMemberByGroupId")
    List<CustomGroupMember> listCustomGroupMemberByGroupId(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("groupId") String groupId);

    /**
     * 根据用户组id和成员类型，获取用户组员列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param groupId 用户组id
     * @param memberType 成员类型，如：Person、Position
     * @return List&lt;CustomGroupMember&gt; 用户组员列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listCustomGroupMemberByGroupIdAndMemberType")
    List<CustomGroupMember> listCustomGroupMemberByGroupIdAndMemberType(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("groupId") String groupId, @RequestParam("memberType") String memberType);

    /**
     * 分页获取自定义用户组
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param page 第几页
     * @param rows 返回多少条数据
     * @return Y9Page&ltMap&lt;String, Object&gt&gt;
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pageCustomGroupByPersonId")
    Y9Page<CustomGroup> pageCustomGroupByPersonId(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 获取人员分页列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组Id
     * @param page 第几页
     * @param rows 返回多少条数据
     * @return Y9Page&ltMap&lt;String, Object&gt&gt; 人员分页列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pageCustomGroupMemberByGroupId")
    Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupId(@RequestParam("tenantId") String tenantId, @RequestParam("groupId") String groupId, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 根据用户组id和成员类型，获取人员分页列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组Id
     * @param memberType 成员类型，如：Person、Position
     * @param page 第几页
     * @param rows 返回多少条数据
     * @return Map&lt;String, Object&gt; 人员分页列表
     * @since 9.6.0
     */
    @Override
    @GetMapping("/pageCustomGroupMemberByGroupIdAndMemberType")
    Y9Page<CustomGroupMember> pageCustomGroupMemberByGroupIdAndMemberType(@RequestParam("tenantId") String tenantId, @RequestParam("groupId") String groupId, @RequestParam("memberType") String memberType, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 删除组成员
     *
     * @param tenantId 租户id
     * @param memberIds ids,多个“,”隔开
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/removeMembers")
    boolean removeMembers(@RequestParam("tenantId") String tenantId, @RequestParam("memberIds") List<String> memberIds);

    /**
     * 保存用户组
     *
     * @param tenantId 租户id
     * @param customGroup 自定义用户组
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveCustomGroup")
    CustomGroup saveCustomGroup(@RequestParam("tenantId") String tenantId, @SpringQueryMap CustomGroup customGroup);

    /**
     * 保存用户组排序
     *
     * @param tenantId 租户id
     * @param sortIds 排序后的用户组Ids,多个“,”隔开
     * @return boolean 是否保存排序成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveCustomGroupOrder")
    boolean saveCustomGroupOrder(@RequestParam("tenantId") String tenantId, @RequestParam("sortIds") List<String> sortIds);

    /**
     * 保存成员排序
     *
     * @param tenantId 租户id
     * @param memberIds 排序后的Ids,多个“,”隔开
     * @return boolean 是否保存排序成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveMemberOrder")
    boolean saveMemberOrder(@RequestParam("tenantId") String tenantId, @RequestParam("memberIds") List<String> memberIds);

    /**
     * 保存用户组
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param personIds 人员Ids,多个“,”隔开
     * @param groupId 用户组Id
     * @param groupName 用户组名称
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveOrUpdateCustomGroup")
    CustomGroup saveOrUpdateCustomGroup(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("personIds") List<String> personIds, @RequestParam("groupId") String groupId, @RequestParam("groupName") String groupName);

    /**
     * 共享用户组
     *
     * @param tenantId 租户id
     * @param personId 人员Id
     * @param personIds 人员Ids,多个“,”隔开
     * @param groupIds 用户组Ids,多个“,”隔开
     * @return boolean 分享是否成功
     * @since 9.6.0
     */
    @Override
    @PostMapping("/shareCustomGroup")
    boolean shareCustomGroup(@RequestParam("tenantId") String tenantId, @RequestParam("personIds") List<String> personIds, @RequestParam("groupIds") List<String> groupIds);
}