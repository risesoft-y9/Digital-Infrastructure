package net.risesoft.api.org;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.dto.CreatePersonDTO;
import net.risesoft.api.platform.org.dto.PersonInfoDTO;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9PersonExt;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.model.platform.Group;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.PersonExt;
import net.risesoft.model.platform.Position;
import net.risesoft.model.platform.Role;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.identity.Y9PersonToRoleService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9PersonExtService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * 人员服务组件
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
@RequestMapping(value = "/services/rest/v1/person", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PersonApiImpl implements PersonApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9GroupService y9GroupService;
    private final Y9PersonExtService y9PersonExtService;
    private final Y9PersonService y9PersonService;
    private final Y9PositionService y9PositionService;
    private final Y9RoleService y9RoleService;
    private final Y9PersonToRoleService y9PersonToRoleService;

    /**
     * 检查登录名是否存在
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param loginName 登录名
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断登录名是否存在
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> checkLoginName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("loginName") @NotBlank String loginName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonService.isLoginNameAvailable(personId, loginName));
    }

    /**
     * 删除人员
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> delete(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonService.delete(personId);
        return Y9Result.success();
    }

    /**
     * 改变人员的禁用状态
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> disable(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonService.changeDisabled(personId);
        return Y9Result.success();
    }

    /**
     * 根据id获得人员对象
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Person> get(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = y9PersonService.findById(personId).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Person, Person.class));
    }

    /**
     * 根据登录名称获得人员对象
     *
     * @param tenantId 租户id
     * @param loginName 人员登录名
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Person> getByLoginName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("loginName") @NotBlank String loginName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = y9PersonService.findByLoginName(loginName).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Person, Person.class));
    }

    /**
     * 根据登录名和父节点id，获取人员信息
     *
     * @param tenantId 租户id
     * @param loginName 登录名称
     * @param parentId 父节点id
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Person> getByLoginNameAndParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("loginName") @NotBlank String loginName, @RequestParam("parentId") @NotBlank String parentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person person = y9PersonService.getByLoginNameAndParentId(loginName, parentId).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(person, Person.class));
    }

    /**
     * 根据人员id获取人员扩展信息
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<PersonExt>} 通用请求返回对象 - data 是人员扩展信息
     * @since 9.6.0
     */
    @Override
    public Y9Result<PersonExt> getPersonExtByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9PersonExt y9PersonExt = y9PersonExtService.findByPersonId(personId).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonExt, PersonExt.class));
    }

    /**
     * 获取Base64加密之后的照片字符串
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是Base64加密之后的照片字符串
     * @since 9.6.0
     */
    @Override
    public Y9Result<String> getPersonPhoto(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonExtService.getEncodePhotoByPersonId(personId));
    }

    /**
     * 获取租户的人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> list(@RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.list(Boolean.FALSE);
        Collections.sort(y9PersonList);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 根据证件类型和证件号码获取人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param idType 证件类型
     * @param idNum 证件号码
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listByIdTypeAndIdNum(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("idType") @NotBlank String idType, @RequestParam("idNum") @NotBlank String idNum) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByIdTypeAndIdNum(idType, idNum, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 根据人员名称获取人员对象列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.2
     */
    @Override
    public Y9Result<List<Person>> listByName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam(name = "name", required = false) String name) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByNameLike(name, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 获取部门下的人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 部门唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByParentId(parentId, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 获取部门下的没有禁用/禁用的人员列表
     *
     * @param tenantId 租户id
     * @param parentId 部门id
     * @param disabled 是否禁用
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listByParentIdAndDisabled(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("disabled") Boolean disabled) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByParentIdAndDisabled(parentId, disabled);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 获取人员所在用户组列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return {@code Y9Result<List<Group>>} 通用请求返回对象 - data 是用户组对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Group>> listGroupsByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Group> y9GroupList = y9GroupService.listByPersonId(personId, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9GroupList, Group.class));
    }

    /**
     * 根据人员id获取父节点列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是父节点对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<OrgUnit>> listParentsByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OrgBase> parentList = y9PersonService.listParents(personId);
        return Y9Result.success(ModelConvertUtil.orgBaseToOrgUnit(parentList));
    }

    /**
     * 根据人员名称获取人员基本信息及其关联的岗位信息
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return {@code Y9Result<List<PersonInfoDTO>>} 通用请求返回对象 - data 是人员基本信息及其关联的岗位信息
     * @since 9.6.2
     */
    @Override
    public Y9Result<List<PersonInfoDTO>> listPersonInfoByName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam(name = "name", required = false) String name) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByNameLike(name, Boolean.FALSE);
        List<PersonInfoDTO> personInfoDTOList = new ArrayList<>();
        if (!y9PersonList.isEmpty()) {
            for (Y9Person person : y9PersonList) {
                PersonInfoDTO personInfoDTO = new PersonInfoDTO();
                personInfoDTO.setPerson(Y9ModelConvertUtil.convert(person, Person.class, "password"));
                if (!Boolean.TRUE.equals(person.getDisabled())) {
                    List<Y9Position> positions = y9PositionService.listByPersonId(person.getId(), Boolean.FALSE);
                    personInfoDTO.setPositionList(Y9ModelConvertUtil.convert(positions, Position.class));
                }
                personInfoDTOList.add(personInfoDTO);
            }
        }
        return Y9Result.success(personInfoDTOList);
    }

    /**
     * 获取人员所在岗位列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Position>> listPositionsByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Position> y9PositionList = y9PositionService.listByPersonId(personId, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PositionList, Position.class));
    }

    /**
     * 递归获取父节点及其所有层级子部门的所有人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 部门id
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listRecursivelyByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = compositeOrgBaseService.listAllPersonsRecursionDownward(parentId, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 根据人员姓名递归获取父节点及其所有层级子部门的所有人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 部门id
     * @param name 人员姓名
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listRecursivelyByParentIdAndName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("name") @NotBlank String name) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList =
            compositeOrgBaseService.searchAllPersonsRecursionDownward(parentId, name, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 获取人员拥有的角色列表
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return {@code Y9Result<List<Role>>} 通用请求返回对象 - data 是获取角色
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Role>> listRoles(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Role> roleList = new ArrayList<>();
        List<Y9PersonToRole> y9PersonToRoleList = y9PersonToRoleService.listByPersonId(personId);
        for (Y9PersonToRole y9PersonToRole : y9PersonToRoleList) {
            Y9Role y9Role = y9RoleService.getById(y9PersonToRole.getRoleId());
            roleList.add(ModelConvertUtil.y9RoleToRole(y9Role));
        }
        return Y9Result.success(roleList);
    }

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
    @Override
    public Y9Result<Person> modifyPassword(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("oldPassword") @NotBlank String oldPassword,
        @RequestParam("newPassword") @NotBlank String newPassword) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = y9PersonService.modifyPassword(personId, oldPassword, newPassword);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Person, Person.class));
    }

    /**
     * 分页模糊搜索人员列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<Person>} 通用请求返回对象 - data 是人员对象
     */
    @Override
    public Y9Page<Person> pageByName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam(required = false) String name, @Validated Y9PageQuery pageQuery) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Page<Y9Person> persons = y9PersonService.pageByNameLike(name, pageQuery);
        List<Person> personList = Y9ModelConvertUtil.convert(persons.getContent(), Person.class);
        return Y9Page.success(persons.getNumber(), persons.getTotalPages(), persons.getTotalElements(), personList,
            "操作成功");
    }

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
    @Override
    public Y9Page<Person> pageByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("disabled") boolean disabled,
        @Validated Y9PageQuery pageQuery) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Page<Y9Person> persons = y9PersonService.pageByParentId(parentId, disabled, pageQuery);
        List<Person> personList = Y9ModelConvertUtil.convert(persons.getContent(), Person.class);
        return Y9Page.success(persons.getNumber(), persons.getTotalPages(), persons.getTotalElements(), personList,
            "操作成功");
    }

    /**
     * 分页模糊搜索父节点下的人员列表
     *
     * @param tenantId 租户ID
     * @param parentId 部门ID
     * @param disabled 是否禁用
     * @param name 用户名称
     * @param pageQuery 分页查询参数
     * @return {@code Y9Page<Person>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Page<Person> pageByParentIdAndName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId, @RequestParam("disabled") boolean disabled,
        @RequestParam(value = "name", required = false) String name, Y9PageQuery pageQuery) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Page<Y9Person> persons = y9PersonService.pageByParentId(parentId, disabled, name, pageQuery);
        List<Person> personList = Y9ModelConvertUtil.convert(persons.getContent(), Person.class);
        return Y9Page.success(persons.getNumber(), persons.getTotalPages(), persons.getTotalElements(), personList,
            "操作成功");
    }

    /**
     * 保存人员头像
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param avatar 人员头像路径
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Person> savePersonAvatar(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("avatar") @NotBlank String avatar) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Person y9Person = y9PersonService.saveAvator(personId, avatar);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Person, Person.class));
    }

    /**
     * 保存用户照片接口
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param photo Base64加密之后的照片字符串
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> savePersonPhoto(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("photo") @NotBlank String photo) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonExtService.savePersonPhoto(personId, photo);
        return Y9Result.success();
    }

    /**
     * 保存用户签名照片接口
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param sign Base64加密之后的签名字符串
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.3
     */
    @Override
    public Y9Result<Object> savePersonSign(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("sign") @NotBlank String sign) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Person y9Person = y9PersonService.findById(personId).orElse(null);
        if (y9Person != null) {
            y9PersonExtService.savePersonSign(y9Person, sign);
        }
        return Y9Result.success();
    }

    /**
     * 保存人员的微信id
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param weixinId 微信id
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是保存的人员对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Person> savePersonWeixinId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("weixinId") @NotBlank String weixinId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Person y9Person = y9PersonService.saveWeixinId(personId, weixinId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Person, Person.class));
    }

    /**
     * 新增或修改人员
     *
     * @param tenantId 租户id
     * @param personDTO 人员对象
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是保存的人员对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Person> savePersonWithExt(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody @Validated CreatePersonDTO personDTO) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = Y9ModelConvertUtil.convert(personDTO, Y9Person.class);
        Y9PersonExt y9PersonExt = Y9ModelConvertUtil.convert(personDTO, Y9PersonExt.class);
        y9Person = y9PersonService.saveOrUpdate(y9Person, y9PersonExt);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Person, Person.class));
    }

}
