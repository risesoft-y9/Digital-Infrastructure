package net.risesoft.api.org;

import jakarta.validation.constraints.NotBlank;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
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
import net.risesoft.entity.Y9PersonExt;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.model.Group;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.PersonExt;
import net.risesoft.model.Position;
import net.risesoft.model.Role;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.identity.Y9PersonToRoleService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9PersonExtService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.service.Y9FileStoreService;
import net.risesoft.y9public.service.role.Y9RoleService;
import net.risesoft.y9public.service.user.Y9UserService;

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
@RequestMapping(value = "/services/rest/person", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class PersonApiImpl implements PersonApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9GroupService y9GroupService;
    private final Y9PersonExtService y9PersonExtService;
    private final Y9PersonService y9PersonService;
    private final Y9PositionService y9PositionService;
    private final Y9RoleService y9RoleService;
    private final Y9UserService y9UserService;
    private final Y9FileStoreService y9FileStoreService;
    private final Y9Properties y9conf;
    private final Y9PersonToRoleService y9PersonToRoleService;

    /**
     * 改变人员的禁用状态
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return true:禁用成功，false:禁用失败
     * @since 9.6.0
     */
    @Override
    public boolean changeDisabled(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonService.changeDisabled(personId);
        return true;
    }

    /**
     * 检查登录名是否存在
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param loginName 登录名
     * @return boolean 用户名是否存在
     * @since 9.6.0
     */
    @Override
    public boolean checkLoginName(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId, @RequestParam("loginName") @NotBlank String loginName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonService.checkLoginNameAvailability(personId, loginName);
    }

    /**
     * 检查手机号码是否存在
     *
     * @param personId 人员id
     * @param mobile 电话号码
     * @return boolean 是否存在
     * @since 9.6.0
     */
    @Override
    public boolean checkMobile(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId, @RequestParam("mobile") @NotBlank String mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = y9PersonService.getById(personId);
        return y9UserService.checkMobile(y9Person, mobile);
    }

    /**
     * 新增人员
     *
     * @param tenantId 租户id
     * @param personJson 人员对象
     * @return Person 人员对象
     * @since 9.6.0
     */
    @Override
    public Person createPerson(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personJson") @NotBlank String personJson) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = Y9JsonUtil.readValue(personJson, Y9Person.class);
        y9Person = y9PersonService.createPerson(y9Person, compositeOrgBaseService.getOrgBase(y9Person.getParentId()));
        return Y9ModelConvertUtil.convert(y9Person, Person.class);
    }

    /**
     * 删除人员
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return true:删除成功，false:删除失败
     * @since 9.6.0
     */
    @Override
    public boolean deleteById(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonService.delete(personId);
        return true;
    }

    /**
     * 根据租户id和人员id获取委办局
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    @Override
    public OrgUnit getBureau(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9OrgBase bureau = y9PersonService.getBureau(personId);
        return ModelConvertUtil.orgBaseToOrgUnit(bureau);
    }

    /**
     * 根据登陆名和父节点id，获取人员信息
     *
     * @param tenantId 租户id
     * @param loginName 登录名称
     * @param parentId 父节点id
     * @return Person 人员对象
     * @since 9.6.0
     */
    @Override
    public Person getByLoginNameAndParentId(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("loginName") @NotBlank String loginName, @RequestParam("parentId") @NotBlank String parentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person person = y9PersonService.getByLoginNameAndParentId(loginName, parentId);
        return Y9ModelConvertUtil.convert(person, Person.class);
    }

    /**
     * 获取人员父节点
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    @Override
    public OrgUnit getParent(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9OrgBase parent = compositeOrgBaseService.getParent(personId);
        return ModelConvertUtil.orgBaseToOrgUnit(parent);
    }

    /**
     * 根据id获得人员对象
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return Person 人员对象
     * @since 9.6.0
     */
    @Override
    public Person getPerson(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = y9PersonService.findById(personId);
        return Y9ModelConvertUtil.convert(y9Person, Person.class);
    }

    /**
     * 根据登录名称和租户id，获得人员对象
     *
     * @param loginName 人员登录名
     * @param tenantId 租户id
     * @return Person 人员对象
     * @since 9.6.0
     */
    @Override
    public Person getPersonByLoginNameAndTenantId(@RequestParam("loginName") @NotBlank String loginName, @RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = y9PersonService.getPersonByLoginName(loginName);
        return Y9ModelConvertUtil.convert(y9Person, Person.class);
    }

    /**
     * 根据人员id，获取人员扩展信息
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return PersonExt
     * @since 9.6.0
     */
    @Override
    public PersonExt getPersonExtByPersonId(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9PersonExt y9PersonExt = y9PersonExtService.findByPersonId(personId);
        return Y9ModelConvertUtil.convert(y9PersonExt, PersonExt.class);
    }

    /**
     * 获取 Base64加密之后的照片字符串
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return String Base64加密之后的照片字符串
     * @since 9.6.0
     */
    @Override
    public String getPersonPhoto(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonExtService.getEncodePhotoByPersonId(personId);
    }

    /**
     * 获取全部人员
     *
     * @param tenantId 租户id
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listAllPersons(@RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.list();
        Collections.sort(y9PersonList);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 根据证件类型和证件号码，获取人员列表
     *
     * @param tenantId 租户id
     * @param idType 证件类型
     * @param idNum 证件号码
     * @return
     * @since 9.6.0
     */
    @Override
    public List<Person> listByIdTypeAndIdNum(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("idType") @NotBlank String idType, @RequestParam("idNum") @NotBlank String idNum) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByIdTypeAndIdNum(idType, idNum);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 根据人员名称 名称、租户id获取人员基本信息
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return List&lt;Person&gt;
     * @since 9.6.2
     */
    @Override
    public List<Person> listByNameLike(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam(name = "name", required = false) String name) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByNameLike(name);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 获取人员所在用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return List<Group> 用户组对象集合
     * @since 9.6.0
     */
    @Override
    public List<Group> listGroups(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Group> y9GroupList = y9GroupService.listByPersonId(personId);
        return Y9ModelConvertUtil.convert(y9GroupList, Group.class);
    }

    /**
     * 根据人员id，获取父节点列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return List&lt;OrgUnit&gt; 父节点对象集合
     * @since 9.6.0
     */
    @Override
    public List<OrgUnit> listParents(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OrgBase> parentList = y9PersonService.listParents(personId);
        return ModelConvertUtil.orgBaseToOrgUnit(parentList);
    }

    /**
     * 根据人员名称 名称、租户id获取人员基本信息，图像，岗位等
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     * @since 9.6.2
     */
    @Override
    public List<Map<String, Object>> listPersonInfoByNameLike(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam(name = "name", required = false) String name) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByNameLike(name);
        List<Map<String, Object>> infoList = new ArrayList<>();
        if (!y9PersonList.isEmpty()) {
            for (Y9Person person : y9PersonList) {
                Map<String, Object> returnMap = new HashMap<>();
                person.setPassword(null);
                returnMap.put("person", person);
                returnMap.put("personExt", y9PersonExtService.findByPersonId(person.getId()));
                returnMap.put("avator", person.getAvator());
                returnMap.put("disabled", person.getDisabled());
                if (!Boolean.TRUE.equals(person.getDisabled())) {
                    List<Y9Position> positions = y9PositionService.listByPersonId(person.getId());
                    if (!positions.isEmpty()) {

                        StringBuilder ids = new StringBuilder();
                        StringBuilder names = new StringBuilder();
                        for (Y9Position position : positions) {
                            ids.append(position.getId() + ",");
                            names.append(position.getName() + ",");
                        }
                        String nameString = names.toString();
                        String idsString = ids.toString();
                        returnMap.put("positionNames", nameString.substring(0, nameString.lastIndexOf(",")));
                        returnMap.put("positionIds", idsString.substring(0, idsString.lastIndexOf(",")));
                    } else {
                        returnMap.put("positionNames", "暂未设置");
                    }
                }
                infoList.add(returnMap);
            }
        }
        return infoList;
    }

    /**
     * 获取人员所在岗位列表
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return List<Position> 岗位对象集合
     * @since 9.6.0
     */
    @Override
    public List<Position> listPositions(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Position> y9PositionList = y9PositionService.listByPersonId(personId);
        return Y9ModelConvertUtil.convert(y9PositionList, Position.class);
    }

    /**
     * 获取角色
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return List<Role> 角色对象集合
     * @since 9.6.0
     */
    @Override
    public List<Role> listRoles(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Role> roleList = new ArrayList<>();
        List<Y9PersonToRole> y9PersonToRoleList = y9PersonToRoleService.listByPersonId(personId);
        for (Y9PersonToRole y9PersonToRole : y9PersonToRoleList) {
            Y9Role y9Role = y9RoleService.findById(y9PersonToRole.getRoleId());
            roleList.add(ModelConvertUtil.y9RoleToRole(y9Role));
        }
        return roleList;
    }

    /**
     * 修改人员密码
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param newPassword 新明文密码
     * @return Person 人员对象
     * @since 9.6.0
     */
    @Override
    public Person modifyPassword(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId, @RequestParam("newPassword") @NotBlank String newPassword) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = y9PersonService.modifyPassword(personId, newPassword);
        return Y9ModelConvertUtil.convert(y9Person, Person.class);
    }

    /**
     * 模糊搜索人员分页列表（不含禁用和删除）
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @param page 页数
     * @param rows 条数
     * @return
     */
    @Override
    public Y9Page<Person> pageByNameLike(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam(required = false) String name, @RequestParam("page") int page, @RequestParam("rows") int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Page<Y9Person> persons = y9PersonService.pageByNameLike(page, rows, name);
        List<Person> personList = Y9ModelConvertUtil.convert(persons.getContent(), Person.class);
        return Y9Page.success(persons.getNumber(), persons.getTotalPages(), persons.getTotalElements(), personList, "操作成功");
    }

    /**
     * 获取父节点下的全部人员
     *
     * @param tenantId 租户ID
     * @param parentId 部门ID
     * @param disabled 是否禁用
     * @param page 页号
     * @param rows 条数
     * @return Y9Page<Person> 人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Page<Person> pageByParentId(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("parentId") @NotBlank String parentId, @RequestParam("disabled") boolean disabled, @RequestParam("page") int page, @RequestParam("rows") int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Page<Y9Person> persons = y9PersonService.pageByParentId(page, rows, parentId, disabled);
        List<Person> personList = Y9ModelConvertUtil.convert(persons.getContent(), Person.class);
        return Y9Page.success(persons.getNumber(), persons.getTotalPages(), persons.getTotalElements(), personList, "操作成功");
    }

    /**
     * 获取父节点下的全部人员
     *
     * @param tenantId 租户ID
     * @param parentId 部门ID
     * @param disabled 是否禁用
     * @param userName 用户名称
     * @param page 页号
     * @param rows 条数
     * @return Y9Page<Person> 人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Page<Person> pageByParentIdAndUserName(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("parentId") @NotBlank String parentId, @RequestParam("disabled") boolean disabled, @RequestParam("userName") @NotBlank String userName, @RequestParam("page") int page,
        @RequestParam("rows") int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Page<Y9Person> persons = y9PersonService.pageByParentId(page, rows, parentId, disabled, userName);
        List<Person> personList = Y9ModelConvertUtil.convert(persons.getContent(), Person.class);
        return Y9Page.success(persons.getNumber(), persons.getTotalPages(), persons.getTotalElements(), personList, "操作成功");
    }

    /**
     * 保存人员
     *
     * @param tenantId 租户id
     * @param personJson 人员对象json
     * @return Person 人员对象
     * @since 9.6.0
     */
    @Override
    public Person savePerson(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personJson") @NotBlank String personJson) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = Y9JsonUtil.readValue(personJson, Y9Person.class);
        Y9PersonExt y9PersonExt = Y9JsonUtil.readValue(personJson, Y9PersonExt.class);
        y9Person = y9PersonService.saveOrUpdate(y9Person, y9PersonExt, compositeOrgBaseService.getOrgBase(y9Person.getParentId()));
        return Y9ModelConvertUtil.convert(y9Person, Person.class);
    }

    /**
     * 保存人员头像
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param avator 人员头像路径
     * @return Person 人员对象
     * @since 9.6.0
     */
    @Override
    public Person savePersonAvator(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId, @RequestParam("avator") @NotBlank String avator) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Person y9Person = y9PersonService.saveAvator(personId, avator);
        return Y9ModelConvertUtil.convert(y9Person, Person.class);
    }

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
    @Override
    public Person savePersonAvatorByBase64(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId, @RequestParam("picnote") @NotBlank String picnote, @RequestParam("fileExt") String fileExt) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = y9PersonService.getById(personId);
        try {
            if (StringUtils.isNotBlank(picnote)) {
                Base64 base64 = new Base64();
                if (picnote.contains("base64,")) {
                    picnote = picnote.split("base64,")[1];
                }
                byte[] data = base64.decode(picnote);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), "avator");
                String fileNewName = y9Person.getLoginName() + "_" + sdf.format(new Date()) + "." + (StringUtils.isBlank(fileExt) ? "png" : fileExt);

                LOGGER.debug("******savePersonAvatorByBase64:{}", fileNewName);

                Y9FileStore y9FileStore = y9FileStoreService.uploadFile(data, fullPath, fileNewName);
                String url = y9conf.getCommon().getOrgBaseUrl() + "/s/" + y9FileStore.getId() + "." + y9FileStore.getFileExt();
                y9Person = y9PersonService.saveAvator(personId, url);
                return Y9ModelConvertUtil.convert(y9Person, Person.class);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 保存用户照片接口
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param photo Base64加密之后的照片字符串
     * @return Boolean 是否保存成功
     * @since 9.6.0
     */
    @Override
    public Boolean savePersonPhoto(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId, @RequestParam("photo") @NotBlank String photo) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonExtService.savePersonPhoto(personId, photo);
        return true;
    }

    /**
     * 保存人员
     *
     * @param tenantId 租户id
     * @param personJson 人员对象
     * @param personextJson 人员扩展信息对象
     * @return Person
     * @since 9.6.0
     */
    @Override
    public Person savePersonWithExt(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personJson") @NotBlank String personJson, @RequestParam("personextJson") @NotBlank String personextJson) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Person y9Person = Y9JsonUtil.readValue(personJson, Y9Person.class);
        Y9PersonExt y9PersonExt = Y9JsonUtil.readValue(personextJson, Y9PersonExt.class);
        y9Person = y9PersonService.saveOrUpdate(y9Person, y9PersonExt, compositeOrgBaseService.getOrgBase(y9Person.getParentId()));
        return Y9ModelConvertUtil.convert(y9Person, Person.class);
    }

    /**
     * 保存人员的微信id
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param weixinId 微信id
     * @return Person 人员对象
     * @since 9.6.0
     */
    @Override
    public Person saveWeixinId(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("personId") @NotBlank String personId, @RequestParam("weixinId") @NotBlank String weixinId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Person y9Person = y9PersonService.saveWeixinId(personId, weixinId);
        return Y9ModelConvertUtil.convert(y9Person, Person.class);
    }

}
