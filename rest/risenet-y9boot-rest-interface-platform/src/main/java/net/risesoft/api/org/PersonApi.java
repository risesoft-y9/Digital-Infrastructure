package net.risesoft.api.org;

import java.util.List;
import java.util.Map;

import net.risesoft.model.Group;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.PersonExt;
import net.risesoft.model.Position;
import net.risesoft.model.Role;
import net.risesoft.pojo.Y9Page;

/**
 * 人员服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface PersonApi {

    /**
     * 改变人员的禁用状态
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return true:禁用成功，false:禁用失败
     * @since 9.6.0
     */
    boolean changeDisabled(String tenantId, String personId);

    /**
     * 检查登录名是否存在
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param loginName 登录名
     * @return boolean 用户名是否存在
     * @since 9.6.0
     */
    boolean checkLoginName(String tenantId, String personId, String loginName);

    /**
     * 检查手机号码是否存在
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param mobile 电话号码
     * @return boolean 是否存在
     * @since 9.6.0
     */
    boolean checkMobile(String tenantId, String personId, String mobile);

    /**
     * 新增人员
     *
     * @param tenantId 租户id
     * @param personJson 人员对象
     * @return Person 人员对象
     * @since 9.6.0
     */
    Person createPerson(String tenantId, String personJson);

    /**
     * 删除人员
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return true:删除成功，false:删除失败
     * @since 9.6.0
     */
    boolean deleteById(String tenantId, String personId);

    /**
     * 根据租户id和人员id获取委办局
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    OrgUnit getBureau(String tenantId, String personId);

    /**
     * 根据登陆名和父节点id，获取人员信息
     *
     * @param tenantId 租户id
     * @param loginName 登录名称
     * @param parentId 父节点id
     * @return Person 人员对象
     * @since 9.6.0
     */
    Person getByLoginNameAndParentId(String tenantId, String loginName, String parentId);

    /**
     * 获取人员父节点
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    OrgUnit getParent(String tenantId, String personId);

    /**
     * 根据id获得人员对象（从缓存中查找）
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return Person 人员对象
     * @since 9.6.0
     */
    Person getPerson(String tenantId, String personId);

    /**
     * 根据登录名称和租户id，获得人员对象
     *
     * @param loginName 人员登录名
     * @param tenantId 租户id
     * @return Person 人员对象
     * @since 9.6.0
     */
    Person getPersonByLoginNameAndTenantId(String loginName, String tenantId);

    /**
     * 根据人员id，获取人员扩展信息
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return PersonExt 人员扩展信息
     * @since 9.6.0
     */
    PersonExt getPersonExtByPersonId(String tenantId, String personId);

    /**
     * 获取 Base64加密之后的照片字符串
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return String Base64加密之后的照片字符串
     * @since 9.6.0
     */
    String getPersonPhoto(String tenantId, String personId);

    /**
     * 获取全部人员
     *
     * @param tenantId 租户id
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    List<Person> listAllPersons(String tenantId);

    /**
     * 根据证件类型和证件号码，获取人员列表
     *
     * @param tenantId 租户id
     * @param idType 证件类型
     * @param idNum 证件号码
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    List<Person> listByIdTypeAndIdNum(String tenantId, String idType, String idNum);

    /**
     * 根据人员名称 名称、租户id获取人员基本信息
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return List&lt;Person&gt;
     * @since 9.6.2
     */
    List<Person> listByNameLike(String tenantId, String name);

    /**
     * 获取人员所在用户组列表
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return List&lt;Group&gt; 用户组对象集合
     * @since 9.6.0
     */
    List<Group> listGroups(String tenantId, String personId);

    /**
     * 根据人员id，获取父节点列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return List&lt;OrgUnit&gt; 父节点对象集合
     * @since 9.6.0
     */
    List<OrgUnit> listParents(String tenantId, String personId);

    /**
     * 根据人员名称 名称、租户id获取人员基本信息，图像，岗位等
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     * @since 9.6.2
     */
    List<Map<String, Object>> listPersonInfoByNameLike(String tenantId, String name);

    /**
     * 获取人员所在岗位列表
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return List&lt;Position&gt; 岗位对象集合
     * @since 9.6.0
     */
    List<Position> listPositions(String tenantId, String personId);

    /**
     * 获取角色
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return List&lt;Role&gt; 角色对象集合
     * @since 9.6.0
     */
    List<Role> listRoles(String tenantId, String personId);

    /**
     * 修改人员密码
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param newPassword 新明文密码
     * @return Person 人员对象
     * @since 9.6.0
     */
    Person modifyPassword(String tenantId, String personId, String newPassword);

    /**
     * 模糊搜索人员分页列表（不含禁用和删除）
     *
     * @param tenantId 租户id
     * @param name 人员名称
     * @param page 页数
     * @param rows 条数
     * @return Y9Page&lt;Person&gt; 人员对象集合
     */
    Y9Page<Person> pageByNameLike(String tenantId, String name, int page, int rows);

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
    Y9Page<Person> pageByParentId(String tenantId, String parentId, boolean disabled, int page, int rows);

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
    Y9Page<Person> pageByParentIdAndUserName(String tenantId, String parentId, boolean disabled, String userName, int page, int rows);

    /**
     * 保存人员
     *
     * @param tenantId 租户id
     * @param personJson 人员对象
     * @return Person 人员对象
     * @since 9.6.0
     */
    Person savePerson(String tenantId, String personJson);

    /**
     * 保存人员头像
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param avator 人员头像路径
     * @return Person 人员对象
     * @since 9.6.0
     */
    Person savePersonAvator(String tenantId, String personId, String avator);

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
    Person savePersonAvatorByBase64(String tenantId, String personId, String picnote, String fileExt);

    /**
     * 保存用户照片接口
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param photo Base64加密之后的照片字符串
     * @return Boolean 是否保存成功
     * @since 9.6.0
     */
    Boolean savePersonPhoto(String tenantId, String personId, String photo);

    /**
     * 保存人员
     *
     * @param tenantId 租户id
     * @param personJson 人员对象
     * @param personextJson 人员扩展信息对象
     * @return Person
     * @since 9.6.0
     */
    Person savePersonWithExt(String tenantId, String personJson, String personextJson);

    /**
     * 保存人员的微信id
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param weixinId 微信id
     * @return Person 人员对象
     * @since 9.6.0
     */
    Person saveWeixinId(String tenantId, String personId, String weixinId);
}
