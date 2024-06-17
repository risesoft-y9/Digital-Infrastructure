package net.risesoft.api.platform.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.platform.org.dto.CreatePersonDTO;
import net.risesoft.api.platform.org.dto.PersonInfoDTO;
import net.risesoft.model.platform.Group;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.PersonExt;
import net.risesoft.model.platform.Position;
import net.risesoft.model.platform.Role;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
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
    @PostMapping("/delete")
    Y9Result<Object> delete(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 改变人员的禁用状态
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/disable")
    Y9Result<Object> disable(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据id获得人员对象
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.0
     */
    @GetMapping("/get")
    Y9Result<Person> get(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据登录名称获得人员对象
     *
     * @param tenantId 租户id
     * @param loginName 人员登录名
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.0
     */
    @GetMapping("/getByLoginName")
    Y9Result<Person> getByLoginName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("loginName") @NotBlank String loginName);

    /**
     * 根据ca证书id获得人员对象
     *
     * @param tenantId 租户id
     * @param caId ca证书id
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.6
     */
    @GetMapping("/getByCaId")
    Y9Result<Person> getByCaId(@RequestParam("tenantId") @NotBlank String tenantId,
                                    @RequestParam("caId") @NotBlank String caId);

    /**
     * 根据登录名和父节点id，获取人员信息
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
     * 根据人员id获取人员扩展信息
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
     * 获取租户的人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/list")
    Y9Result<List<Person>> list(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 根据证件类型和证件号码获取人员列表（不包含禁用）
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
     * 根据人员名称获取人员对象列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.2
     */
    @GetMapping("/listByName")
    Y9Result<List<Person>> listByName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam(name = "name", required = false) String name);

    /**
     * 获取部门下的人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 部门唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByParentId")
    Y9Result<List<Person>> listByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId);

    /**
     * 获取部门下的没有禁用/禁用的人员列表
     *
     * @param tenantId 租户id
     * @param parentId 部门id
     * @param disabled 是否禁用
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByParentIdAndDisabled")
    Y9Result<List<Person>> listByParentIdAndDisabled(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("disabled") Boolean disabled);

    /**
     * 获取人员所在用户组列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return {@code Y9Result<List<Group>>} 通用请求返回对象 - data 是用户组对象集合
     * @since 9.6.0
     */
    @GetMapping("/listGroupsByPersonId")
    Y9Result<List<Group>> listGroupsByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据人员id获取父节点列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是父节点对象集合
     * @since 9.6.0
     */
    @GetMapping("/listParentsByPersonId")
    Y9Result<List<OrgUnit>> listParentsByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据人员名称获取人员基本信息及其关联的岗位信息
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return {@code Y9Result<List<PersonInfoDTO>>} 通用请求返回对象 - data 是人员基本信息及其关联的岗位信息
     * @since 9.6.2
     */
    @GetMapping("/listPersonInfoByName")
    Y9Result<List<PersonInfoDTO>> listPersonInfoByName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam(name = "name", required = false) String name);

    /**
     * 获取人员所在岗位列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPositionsByPersonId")
    Y9Result<List<Position>> listPositionsByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 递归获取父节点及其所有层级子部门的所有人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 父节点id
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listRecursivelyByParentId")
    Y9Result<List<Person>> listRecursivelyByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId);

    /**
     * 根据人员姓名递归获取父节点及其所有层级子部门的所有人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 部门id
     * @param name 人员姓名
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listRecursivelyByParentIdAndName")
    Y9Result<List<Person>> listRecursivelyByParentIdAndName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("name") @NotBlank String name);

    /**
     * 获取人员拥有的角色列表
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
     * 分页模糊搜索人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<Person>} 通用请求返回对象 - data 是人员对象
     */
    @GetMapping("/pageByName")
    Y9Page<Person> pageByName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam(required = false) String name, @Validated Y9PageQuery pageQuery);

    /**
     * 分页获取父节点下的人员
     *
     * @param tenantId 租户ID
     * @param parentId 部门ID
     * @param disabled 是否禁用
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<Person>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/pageByParentId")
    Y9Page<Person> pageByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("disabled") boolean disabled,
        @Validated Y9PageQuery pageQuery);

    /**
     * 分页模糊搜索父节点下的人员列表
     *
     * @param tenantId 租户ID
     * @param parentId 部门ID
     * @param disabled 是否禁用
     * @param name 人员名称
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<Person>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/pageByParentIdAndName")
    Y9Page<Person> pageByParentIdAndName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("disabled") boolean disabled,
        @RequestParam(value = "name", required = false) String name, Y9PageQuery pageQuery);

    /**
     * 保存人员头像
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param avatar 人员头像路径
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.0
     */
    @PostMapping("/savePersonAvatar")
    Y9Result<Person> savePersonAvatar(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("avatar") @NotBlank String avatar);

    /**
     * 保存人员照片接口
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
     * 保存人员签名照片接口
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param sign Base64加密之后的签名字符串
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.3
     */
    @PostMapping("/savePersonSign")
    Y9Result<Object> savePersonSign(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("sign") @NotBlank String sign);

    /**
     * 保存人员的微信id
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param weixinId 微信id
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是保存的人员对象
     * @since 9.6.0
     */
    @PostMapping("/savePersonWeixinId")
    Y9Result<Person> savePersonWeixinId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("weixinId") @NotBlank String weixinId);

    /**
     * 新增或修改人员
     *
     * @param tenantId 租户id
     * @param personDTO 人员对象
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是保存的人员对象
     * @since 9.6.0
     */
    @PostMapping("/savePersonWithExt")
    Y9Result<Person> savePersonWithExt(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody @Validated CreatePersonDTO personDTO);

}
