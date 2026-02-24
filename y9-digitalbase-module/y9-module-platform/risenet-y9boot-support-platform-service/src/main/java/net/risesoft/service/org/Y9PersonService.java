package net.risesoft.service.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.PersonExt;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.PersonQuery;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PersonService {

    /**
     * 检查caid是否可用
     *
     * @param personId 人员id
     * @param caid ca唯一标识
     * @return boolean
     */
    boolean isCaidAvailable(String personId, String caid);

    /**
     * 根据personIds和parentId添加人员集合
     *
     * @param parentId 父节点id
     * @param personIds 人员id数组
     * @return {@code List<Person>}
     */
    List<Person> addPersons(String parentId, List<String> personIds);

    /**
     * 根据人员id，改变人员禁用状态
     *
     * @param id 唯一标识
     * @return {@link Person}
     */
    Person changeDisabled(String id);

    /**
     * 根据guidPath和Boolean值查询
     *
     * @param guidPath guid路径
     * @return long
     */
    long countByGuidPathLikeAndDisabledAndDeletedFalse(String guidPath);

    /**
     * 根据必须字段创建人员
     *
     * @param parentId 父ID
     * @param name 名字
     * @param loginName 登录名
     * @param mobile 手机号
     * @return {@link Person}
     */
    Person create(String parentId, String name, String loginName, String mobile);

    /**
     * 根据id数组，删除人员
     *
     * @param ids id数组
     */
    void delete(List<String> ids);

    /**
     * 根据主键id删除人员实例(人员与组，与角色，与岗位的关联关系都删掉)
     *
     * @param id id
     */
    void delete(String id);

    /**
     * 根据父节点id，删除人员
     *
     * @param parentId 父节点id
     */
    void deleteByParentId(String parentId);

    /**
     * 根据id查找人员对象
     *
     * @param id 唯一标识
     * @return {@code Optional<Person>}人员对象 或 null
     */
    Optional<Person> findById(String id);

    /**
     * 根据登录名获取人员
     *
     * @param loginName 登录名
     * @return {@code Optional<Person>}
     */
    Optional<Person> findByLoginName(String loginName);

    /**
     * 根据ca证书Id获取人员
     *
     * @param caId ca证书Id
     * @return {@code Optional<Person>}
     */
    Optional<Person> findByCaId(String caId);

    /**
     * 查找 guidPath 包含传入参数的对应人的 id
     *
     * @param guidPath guid path
     * @return {@code List<String>}
     */
    List<String> findIdByGuidPathStartingWith(String guidPath);

    /**
     * 根据主键id获取人员实例
     *
     * @param id 唯一标识
     * @return {@link Person} 人员对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Person getById(String id);

    /**
     * 根据登陆名和父节点id，获取人员信息
     *
     * @param loginName 登录名
     * @param parentId 父节点id
     * @return {@code Optional<Person>}
     */
    Optional<Person> getByLoginNameAndParentId(String loginName, String parentId);

    /**
     * 根据登录名、租户id获取人员
     *
     * @param loginName tenantId
     * @return {@link Person}
     */
    Person getPersonByLoginName(String loginName);

    /**
     * 判断用户名是否可用
     *
     * @param personId 人员id
     * @param loginName 登录名
     * @return boolean
     */
    boolean isLoginNameAvailable(String personId, String loginName);

    /**
     * 查找所有人员
     *
     * @param disabled 是否包含禁用的人员
     * @return {@code List<Person>}
     */
    List<Person> list(Boolean disabled);

    /**
     * 根据 id 列表获取人员
     *
     * @param ids IDs
     * @return {@code List<Person> }
     */
    List<Person> listByIds(List<String> ids);

    /**
     * 查询所有人员
     *
     * @return {@code List<Person>}
     */
    List<Person> listAll();

    /**
     * 根据用户组节点id,获取本组的人员列表
     *
     * @param groupId 用户组id
     * @param disabled 是否包含禁用的人员
     * @return {@code List<Person>}
     */
    List<Person> listByGroupId(String groupId, Boolean disabled);

    /**
     * 根据idType和idNum查询
     *
     * @param idType 证件类型
     * @param idNum 证件号
     * @param disabled 是否已禁用
     * @return {@code List<Person>}
     */
    List<Person> listByIdTypeAndIdNum(String idType, String idNum, Boolean disabled);

    /**
     * 根据名称查询
     *
     * @param name 姓名
     * @param disabled 是否已禁用
     * @return {@code List<Person>}
     */
    List<Person> listByNameLike(String name, Boolean disabled);

    /**
     * 根据父id及禁用状态查询人员
     *
     * @param parentId 父节点id
     * @param disabled 是否已禁用
     * @return {@code List<Person>}
     */
    List<Person> listByParentId(String parentId, Boolean disabled);

    /**
     * 根据岗位id,获取人员列表
     *
     * @param positionId 岗位id
     * @param disabled 是否已禁用
     * @return {@code List<Person>}
     */
    List<Person> listByPositionId(String positionId, Boolean disabled);

    /**
     * 获取人员的所有父节点 <br>
     * 一般只会有一个，一人多账号的情况可能会有多个
     *
     * @param personId 人员id
     * @return {@code List<Y9OrgBase>}
     */
    List<OrgUnit> listParents(String personId);

    /**
     * 修改人员密码
     *
     * @param id 人员id
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return {@link Person}
     */
    Person modifyPassword(String id, String oldPassword, String newPassword);

    /**
     * 移动
     *
     * @param id 人员id
     * @param parentId 父节点id
     * @return {@link Person}
     */
    Person move(String id, String parentId);

    /**
     * 按照tabindexs的顺序重新排序人员列表
     *
     * @param personIds 人员id数组
     * @return {@code List<Y9OrgBase>}
     */
    List<Person> order(List<String> personIds);

    /**
     * 重置默认密码
     *
     * @param id 人员id
     */
    void resetDefaultPassword(String id);

    /**
     * 保存人员头像
     *
     * @param id 人员id
     * @param avatorUrl 头像路径
     * @return {@link Person}
     */
    Person saveAvator(String id, String avatorUrl);

    /**
     * 保存或者修改此岗位的信息
     *
     * @param person 人员对象
     * @param personExt 人员扩展信息对象
     * @return {@link Person}
     */
    Person saveOrUpdate(Person person, PersonExt personExt);

    /**
     * 保存或更新
     *
     * @param person 人员对象
     * @param ext 人员扩展信息对象
     * @param positionIds 岗位id列表 用于关联已有岗位
     * @param jobIds 职位id列表 通过职位新增岗位关联
     * @return {@link Person}
     */
    Person saveOrUpdate(Person person, PersonExt ext, List<String> positionIds, List<String> jobIds);

    /**
     * 保存或者更新人员扩展信息
     *
     * @param id 人员id
     * @param properties 扩展属性
     * @return {@link Person}
     */
    Person saveProperties(String id, String properties);

    /**
     * 修改微信 id
     *
     * @param id 人员 id
     * @param weixinId 微信 id
     * @return {@link Person}
     */
    Person saveWeixinId(String id, String weixinId);

    /**
     * 更新排序序列号
     *
     * @param id 人员id
     * @param tabIndex 排序序列号
     * @return {@link Person}
     */
    Person updateTabIndex(String id, int tabIndex);

    /**
     * 分页查询人员
     *
     * @param personQuery 人员查询条件
     * @param pageQuery 分页条件
     * @return {@code Page<Person> }
     */
    Y9Page<Person> page(PersonQuery personQuery, Y9PageQuery pageQuery);
}
