package net.risesoft.api.org;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.Group;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.PersonExt;
import net.risesoft.model.Position;
import net.risesoft.model.Role;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 人员服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface PersonApi {

    /**
     * 改变人员的禁用状态
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return true:禁用成功，false:禁用失败
     * @since 9.6.0
     */
    @GetMapping("/changeDisabled")
    Y9Result<Object> changeDisabled(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 检查用户名
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param loginName 登录名
     * @return boolean 用户名是否存在
     * @since 9.6.0
     */
    @GetMapping("/checkLoginName")
    Y9Result<Boolean> checkLoginName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("loginName") @NotBlank String loginName);

    /**
     * 新增人员
     *
     * @param tenantId 租户id
     * @param personJson 人员对象
     * @return Person 人员对象
     * @since 9.6.0
     */
    @PostMapping("/createPerson")
    Y9Result<Person> createPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personJson") @NotBlank String personJson);

    /**
     * 删除人员
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return true:删除成功，false:删除失败
     * @since 9.6.0
     */
    @PostMapping("/deleteById")
    Y9Result<Object> deleteById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据租户id和人员id获取委办局
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return OrgUnit 组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @GetMapping("/getBureau")
    Y9Result<OrgUnit> getBureau(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据登陆名和父节点id，获取人员信息
     *
     * @param tenantId 租户id
     * @param loginName 登录名称
     * @param parentId 父节点id
     * @return Person 人员对象
     * @since 9.6.0
     */
    @GetMapping("/getByLoginNameAndParentId")
    Y9Result<Person> getByLoginNameAndParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("loginName") @NotBlank String loginName, @RequestParam("parentId") @NotBlank String parentId);

    /**
     * 获取人员父节点
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return OrgUnit 组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @GetMapping("/getParent")
    Y9Result<OrgUnit> getParent(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据id获得人员对象
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return Person 人员对象
     * @since 9.6.0
     */
    @GetMapping("/getPerson")
    Y9Result<Person> getPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据登录名称和租户id，获得人员对象
     *
     * @param loginName 人员登录名
     * @param tenantId 租户id
     * @return Person 人员对象
     * @since 9.6.0
     */
    @GetMapping("/getPersonByLoginNameAndTenantId")
    Y9Result<Person> getPersonByLoginNameAndTenantId(@RequestParam("loginName") @NotBlank String loginName,
        @RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 根据人员id，获取人员扩展信息
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return PersonExt
     * @since 9.6.0
     */
    @GetMapping("/getPersonExtByPersonId")
    Y9Result<PersonExt> getPersonExtByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 获取 Base64加密之后的照片字符串
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return String Base64加密之后的照片字符串
     * @since 9.6.0
     */
    @GetMapping("/getPersonPhoto")
    Y9Result<String> getPersonPhoto(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 获取全部人员
     *
     * @param tenantId 租户id
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listAllPersons")
    Y9Result<List<Person>> listAllPersons(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 根据证件类型和证件号码，获取人员列表
     *
     * @param tenantId 租户id
     * @param idType 证件类型
     * @param idNum 证件号码
     * @return List&lt;Person&gt;
     * @since 9.6.0
     */
    @GetMapping("/listByIdTypeAndIdNum")
    Y9Result<List<Person>> listByIdTypeAndIdNum(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("idType") @NotBlank String idType, @RequestParam("idNum") @NotBlank String idNum);

    /**
     * 根据人员名称、租户id获取人员基本信息
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return List&lt;Person&gt;
     * @since 9.6.2
     */
    @GetMapping("/listByNameLike")
    Y9Result<List<Person>> listByNameLike(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam(name = "name", required = false) String name);

    /**
     * 获取人员所在用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return List&lt;Group&gt; 用户组对象集合
     * @since 9.6.0
     */
    @GetMapping("/listGroups")
    Y9Result<List<Group>> listGroups(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据人员id，获取父节点列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return List&lt;OrgUnit&gt; 父节点对象集合
     * @since 9.6.0
     */
    @GetMapping("/listParents")
    Y9Result<List<OrgUnit>> listParents(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据人员名称 名称、租户id获取人员基本信息，图像，岗位等
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     * @since 9.6.2
     */
    @GetMapping("/listPersonInfoByNameLike")
    List<Map<String, Object>> listPersonInfoByNameLike(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam(name = "name", required = false) String name);

    /**
     * 获取人员所在岗位列表
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return List&lt;Position&gt; 岗位对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPositions")
    Y9Result<List<Position>> listPositions(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 获取角色
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return List 角色对象集合
     * @since 9.6.0
     */
    @GetMapping("/listRoles")
    Y9Result<List<Role>> listRoles(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 修改人员密码
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param newPassword 新明文密码
     * @return Person 人员对象
     * @since 9.6.0
     */
    @PostMapping("/modifyPassword")
    Y9Result<Person> modifyPassword(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("newPassword") @NotBlank String newPassword);

    /**
     * 模糊搜索人员分页列表（不含禁用和删除）
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @param page 页数
     * @param rows 条数
     * @return Y9Page&lt;Person&gt;
     */
    @GetMapping("/pageByNameLike")
    Y9Page<Person> pageByNameLike(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam(required = false) String name, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 获取父节点下的全部人员
     *
     * @param tenantId 租户ID
     * @param parentId 部门ID
     * @param disabled 是否禁用
     * @param page 页号
     * @param rows 条数
     * @return Y9Page&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/pageByParentId")
    Y9Page<Person> pageByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("disabled") boolean disabled,
        @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 获取父节点下的全部人员
     *
     * @param tenantId 租户ID
     * @param parentId 部门ID
     * @param disabled 是否禁用
     * @param userName 用户名称
     * @param page 页号
     * @param rows 条数
     * @return Y9Page&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/pageByParentIdAndUserName")
    Y9Page<Person> pageByParentIdAndUserName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("disabled") boolean disabled,
        @RequestParam("userName") @NotBlank String userName, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     * 保存人员
     *
     * @param tenantId 租户id
     * @param personJson 人员对象
     * @return Person 人员对象
     * @since 9.6.0
     */
    @PostMapping("/savePerson")
    Y9Result<Person> savePerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personJson") @NotBlank String personJson);

    /**
     * 保存人员头像
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param avator 人员头像路径
     * @return Person 人员对象
     * @since 9.6.0
     */
    @PostMapping("/savePersonAvator")
    Y9Result<Person> savePersonAvator(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("avator") @NotBlank String avator);

    /**
     * 保存人员头像(Base64)
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param picnote 人员头像
     * @param fileExt 文件类型(png,jpg...)
     * @return Person 人员对象
     * @since 9.6.0
     */
    @PostMapping("/savePersonAvatorByBase64")
    Y9Result<Person> savePersonAvatorByBase64(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("picnote") @NotBlank String picnote,
        @RequestParam("fileExt") String fileExt);

    /**
     * 保存用户照片接口
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param photo Base64加密之后的照片字符串
     * @return Boolean 是否保存成功
     * @since 9.6.0
     */
    @PostMapping("/savePersonPhoto")
    Y9Result<Object> savePersonPhoto(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("photo") @NotBlank String photo);

    /**
     * 保存人员
     *
     * @param tenantId 租户id
     * @param personJson 人员对象
     * @param personextJson 人员扩展信息对象
     * @return Person
     * @since 9.6.0
     */
    @PostMapping("/savePersonWithExt")
    Y9Result<Person> savePersonWithExt(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personJson") @NotBlank String personJson,
        @RequestParam("personextJson") @NotBlank String personextJson);

    /**
     * 保存人员的微信id
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param weixinId 微信id
     * @return Person 人员对象
     * @since 9.6.0
     */
    @PostMapping("/saveWeixinId")
    Y9Result<Person> saveWeixinId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("weixinId") @NotBlank String weixinId);

}
