package net.risesoft.api.org;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.Group;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;

/**
 * 用户组服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface GroupApi {

    /**
     * 向用户组添加人员
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @param personId 人员ID
     * @return boolean 时候添加成功
     * @since 9.6.0
     */
    @GetMapping("/addPerson2Group")
    boolean addPerson2Group(@RequestParam("tenantId") String tenantId, @RequestParam("groupId") String groupId, @RequestParam("personId") String personId);

    /**
     * 创建用户组
     *
     * @param tenantId 租户id
     * @param groupJson 用户组对象
     * @return Group 用户组对象
     * @since 9.6.0
     */
    @PostMapping("/createGroup")
    Group createGroup(@RequestParam("tenantId") String tenantId, @RequestParam("groupJson") String groupJson);

    /**
     * 删除用户组
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @return true 删除成功，false 删除失败
     * @since 9.6.0
     */
    @GetMapping("/deleteGroup")
    boolean deleteGroup(@RequestParam("tenantId") String tenantId, @RequestParam("groupId") String groupId);

    /**
     * 根据id获得用户组对象
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return Group 用户组对象
     * @since 9.6.0
     */
    @GetMapping("/get")
    Group getGroup(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("groupId") @NotBlank String groupId);

    /**
     * 获取用户组父节点
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return OrgUnit 组织架构节点对象
     * @since 9.6.0
     */
    @GetMapping("/getParent")
    OrgUnit getParent(@RequestParam("tenantId") String tenantId, @RequestParam("groupId") String groupId);

    /**
     * 根据租户id和路径获取所有用户组
     *
     * @param tenantId 租户id
     * @param dn 路径
     * @return List 用户组对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByDn")
    List<Group> listByDn(@RequestParam("tenantId") String tenantId, @RequestParam("dn") String dn);

    /**
     * 获取组内的人员列表
     *
     * @param tenantId 租户id
     * @param groupId 用户组唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPersons")
    List<Person> listPersons(@RequestParam("tenantId") String tenantId, @RequestParam("groupId") String groupId);

    /**
     * 从用户组移除人员
     *
     * @param tenantId 租户ID
     * @param groupId 用户组ID
     * @param personId 人员ID
     * @return boolean 是否移除成功
     * @since 9.6.0
     */
    @GetMapping("/removePerson")
    boolean removePerson(@RequestParam("tenantId") String tenantId, @RequestParam("groupId") String groupId, @RequestParam("personId") String personId);

    /**
     * 更新用户组
     *
     * @param tenantId 租户id
     * @param groupJson 用户组对象
     * @return Group 用户组对象
     * @since 9.6.0
     */
    @PostMapping("/updateGroup")
    Group updateGroup(@RequestParam("tenantId") String tenantId, @RequestParam("groupJson") String groupJson);
}