package net.risesoft.api.org;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.org.dto.CreatePersonDTO;
import net.risesoft.api.org.dto.PersonInfoDTO;
import net.risesoft.model.platform.Group;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.PersonExt;
import net.risesoft.model.platform.Position;
import net.risesoft.model.platform.Role;
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
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/changeDisabled")
    Y9Result<Object> changeDisabled(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 检查登录名是否存在
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param loginName 登录名
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断登录名是否存在
     * @since 9.6.0
     */
    @GetMapping("/checkLoginName")
    Y9Result<Boolean> checkLoginName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("loginName") @NotBlank String loginName);

    /**
     * 删除人员
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
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
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
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
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
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
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
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
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
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
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
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
     * @return {@code Y9Result<PersonExt>} 通用请求返回对象 - data 是人员扩展信息
     * @since 9.6.0
     */
    @GetMapping("/getPersonExtByPersonId")
    Y9Result<PersonExt> getPersonExtByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 获取Base64加密之后的照片字符串
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是Base64加密之后的照片字符串
     * @since 9.6.0
     */
    @GetMapping("/getPersonPhoto")
    Y9Result<String> getPersonPhoto(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 获取全部人员
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
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
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByIdTypeAndIdNum")
    Y9Result<List<Person>> listByIdTypeAndIdNum(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("idType") @NotBlank String idType, @RequestParam("idNum") @NotBlank String idNum);

    /**
     * 根据人员名称、租户id获取人员对象集合
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
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
     * @return {@code Y9Result<List<Group>>} 通用请求返回对象 - data 是用户组对象集合
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
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是父节点对象集合
     * @since 9.6.0
     */
    @GetMapping("/listParents")
    Y9Result<List<OrgUnit>> listParents(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据人员名称、租户id获取人员基本信息，图像，岗位等
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     * @since 9.6.2
     */
    @GetMapping("/listPersonInfoByNameLike")
    Y9Result<List<PersonInfoDTO>> listPersonInfoByNameLike(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam(name = "name", required = false) String name);

    /**
     * 获取人员所在岗位列表
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
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
     * @return {@code Y9Result<List<Role>>} 通用请求返回对象 - data 是获取角色
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
     * @param oldPassword 旧明文密码
     * @param newPassword 新明文密码
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.0
     */
    @PostMapping("/modifyPassword")
    Y9Result<Person> modifyPassword(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("oldPassword") @NotBlank String oldPassword,
        @RequestParam("newPassword") @NotBlank String newPassword);

    /**
     * 模糊搜索人员分页列表（不含禁用和删除）
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @param page 页数
     * @param rows 条数
     * @return {@code Y9Page<Person>} 通用请求返回对象 - data 是人员对象
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
     * @return {@code Y9Page<Person>} 通用请求返回对象 - data 是人员对象集合
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
     * @param name 用户名称
     * @param page 页号
     * @param rows 条数
     * @return {@code Y9Page<Person>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/pageByParentIdAndName")
    Y9Page<Person> pageByParentIdAndName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("disabled") boolean disabled,
        @RequestParam("name") @NotBlank String name, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 保存人员头像
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param avator 人员头像路径
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.0
     */
    @PostMapping("/savePersonAvator")
    Y9Result<Person> savePersonAvator(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("avator") @NotBlank String avator);

    /**
     * 保存用户照片接口
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param photo Base64加密之后的照片字符串
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/savePersonPhoto")
    Y9Result<Object> savePersonPhoto(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("photo") @NotBlank String photo);

    /**
     * 新增或修改人员
     *
     * @param tenantId 租户id
     * @param personDTO 人员对象
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是保存的人员对象
     * @since 9.6.0
     */
    @PostMapping("/savePersonWithExt")
    Y9Result<Person> savePerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody @Validated CreatePersonDTO personDTO);

    /**
     * 保存人员的微信id
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param weixinId 微信id
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是保存的人员对象
     * @since 9.6.0
     */
    @PostMapping("/saveWeixinId")
    Y9Result<Person> saveWeixinId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("weixinId") @NotBlank String weixinId);

}
