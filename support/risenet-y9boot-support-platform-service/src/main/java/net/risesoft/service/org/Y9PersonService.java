package net.risesoft.service.org;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9PersonExt;
import net.risesoft.model.Message;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PersonService {

    /**
     * 根据personIds和parentId添加人员集合
     *
     * @param parentId 父节点id
     * @param personIds 人员id数组
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> addPersons(String parentId, List<String> personIds);

    /**
     * 用户认证
     *
     * @param loginName 登录名
     * @param password 密码
     * @return {@link Message}
     */
    Message authenticate(String loginName, String password);

    /**
     * 用户认证
     *
     * @param tenantName 租户名
     * @param loginName 登录名
     * @param password 密码
     * @return {@link Message}
     */
    Message authenticate2(String tenantName, String loginName, String password);

    /**
     * 用户认证
     *
     * @param tenantShortName 租户英文名
     * @param loginName 登录名
     * @param password 密码
     * @return {@link Message}
     */
    Message authenticate3(String tenantShortName, String loginName, String password);

    /**
     * 用户认证
     *
     * @param tenantShortName 租户英文名
     * @param loginName 登录名
     * @return {@link Message}
     */
    Message authenticate4(String tenantShortName, String loginName);

    /**
     * 用户认证
     *
     * @param tenantShortName 租户英文名
     * @param mobile 手机号
     * @param password 密码
     * @return {@link Message}
     */
    Message authenticate5(String tenantShortName, String mobile, String password);

    /**
     * 用户认证
     *
     * @param tenantShortName 租户英文名
     * @param loginName 登录名
     * @param password 密码
     * @param parentId 父节点id
     * @return {@link Message}
     */
    Message authenticate6(String tenantShortName, String loginName, String password, String parentId);

    /**
     * 根据人员id，改变人员禁用状态
     *
     * @param id 唯一标识
     * @return {@link Y9Person}
     */
    Y9Person changeDisabled(String id);

    /**
     * 判断邮箱是否可用
     *
     * @param email 电子邮箱
     * @return boolean
     */
    boolean checkEmailAvailability(String email);

    /**
     * 判断用户名是否可用
     *
     * @param personId
     * @param loginName 登录名
     * @return boolean
     */
    boolean checkLoginNameAvailability(String personId, String loginName);

    /**
     * 根据guidPath和Boolean值查询
     *
     * @param guidPath guid路径
     * @return long
     */
    long countByGuidPathLikeAndDisabledAndDeletedFalse(String guidPath);

    /**
     * 根据父节点部门id，获取子节点的人员数量
     *
     * @param parentId 父节点id
     * @return long
     */
    long countByParentId(String parentId);

    /**
     * 新建人员
     *
     * @param person 人员对象
     * @param parent 父节点对象
     * @return {@link Y9Person}
     */
    Y9Person createPerson(Y9Person person, Y9OrgBase parent);

    /**
     * 根据主键id删除人员实例(人员与组，与角色，与岗位的关联关系都删掉)
     *
     * @param id id
     */
    void delete(String id);

    /**
     * 根据id数组，删除人员
     *
     * @param ids id数组
     */
    void delete(List<String> ids);

    /**
     * 根据父节点id，删除人员
     *
     * @param parentId 父节点id
     */
    void deleteByParentId(String parentId);

    /**
     * 根据id判断人员是否存在
     *
     * @param id 唯一标识
     * @return boolean
     */
    boolean existsById(String id);

    /**
     * 根据id查找人员对象
     *
     * @param id 唯一标识
     * @return 人员对象 或 null
     */
    Optional<Y9Person> findById(String id);

    /**
     * 根据人员id获取委办局id
     *
     * @param id 唯一标识
     * @return {@link Y9OrgBase}
     */
    Y9OrgBase getBureau(String id);

    /**
     * 根据主键id获取人员实例
     *
     * @param id 唯一标识
     * @return 人员对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Person getById(String id);

    /**
     * 根据登陆名和父节点id，获取人员信息
     *
     * @param loginName 登录名
     * @param parentId 父节点id
     * @return {@link Y9Person}
     */
    Optional<Y9Person> getByLoginNameAndParentId(String loginName, String parentId);

    /**
     * 根据登录名获取人员
     *
     * @param loginName 登录名
     * @return {@link Y9Person}
     */
    Optional<Y9Person> getPersonByLoginName(String loginName);

    /**
     * 根据登录名、租户id获取人员
     *
     * @param tenantId 租户id
     * @param loginName tenantId
     * @return {@link Y9Person}
     */
    Y9Person getPersonByLoginNameAndTenantId(String loginName, String tenantId);

    /**
     * 根据手机号码获取人员
     *
     * @param mobile 手机号码
     * @return {@link Y9Person}
     */
    Y9Person getPersonByMobile(String mobile);

    /**
     * 查找所有人员
     *
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> list();

    /**
     * 查询所有未被删除的人员
     *
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> listAll();

    /**
     * 根据父节点id，获取子节点下的所有人员，包括用户组和岗位下的人员
     *
     * @param parentId 父节点id
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> listAllByParentId(String parentId);

    /**
     * 根据guidPath（模糊查询），获取人员列表
     *
     * @param guidPath guid路径
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> listByDisabledAndDeletedAndGuidPathLike(String guidPath);

    /**
     * 根据用户组节点id,获取本组的人员列表
     *
     * @param groupId 用户组id
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> listByGroupId(String groupId);

    /**
     * 根据idType和idNum查询
     *
     * @param idType 证件类型
     * @param idNum 证件号
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> listByIdTypeAndIdNum(String idType, String idNum);

    /**
     * 根据名称查询
     *
     * @param name 姓名
     * @return List<ORGPerson>
     */
    List<Y9Person> listByNameLike(String name);

    /**
     * 根据名称查询
     *
     * @param name 姓名
     * @param dnName dn
     * @return List<ORGPerson>
     */
    List<Y9Person> listByNameLike(String name, String dnName);

    /**
     * 根据父节点id,获取本层级的岗位列表
     *
     * @param parentId 父节点id
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> listByParentId(String parentId);

    /**
     * 根据父id及禁用状态查询人员
     *
     * @param parentId 父节点id
     * @param disabled 是否已禁用
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> listByParentIdAndDisabled(String parentId, boolean disabled);

    /**
     * 根据岗位id,获取人员列表
     *
     * @param positionId 岗位id
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> listByPositionId(String positionId);

    /**
     * 获取人员的所有父节点 <br/>
     * 一般只会有一个，一人多账号的情况可能会有多个
     *
     * @param personId 人员id
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> listParents(String personId);

    /**
     * 修改人员密码
     *
     * @param personId 人员id
     * @param newPassword 新密码
     * @return {@link Y9Person}
     */
    Y9Person modifyPassword(String personId, String newPassword);

    /**
     * 移动
     *
     * @param personId 人员id
     * @param parentId 父节点id
     * @return {@link Y9Person}
     */
    Y9Person move(String personId, String parentId);

    /**
     * 按照tabindexs的顺序重新排序人员列表
     *
     * @param personIds 人员id数组
     * @return {@link List}<{@link Y9OrgBase}>
     */
    List<Y9OrgBase> order(List<String> personIds);

    /**
     * 根据名称模糊查询人员
     *
     * @param page 页数
     * @param rows 每页行数
     * @param userName 人员姓名
     * @return {@link Page}<{@link Y9Person}>
     */
    Page<Y9Person> pageByNameLike(int page, int rows, String userName);

    /**
     * 根据父节点查询，本部门下的人员
     *
     * @param page 页数
     * @param rows 每页行数
     * @param parentId 父节点id
     * @param disabled 是否已禁用
     * @return {@link Page}<{@link Y9Person}>
     */
    Page<Y9Person> pageByParentId(int page, int rows, String parentId, boolean disabled);

    /**
     * 根据父节点id、人员禁用状态及名称模糊查询本部门下人员
     *
     * @param page 页数
     * @param rows 每页行数
     * @param parentId 父节点id
     * @param disabled 是否已禁用
     * @param userName 人员姓名
     * @return {@link Page}<{@link Y9Person}>
     */
    Page<Y9Person> pageByParentId(int page, int rows, String parentId, boolean disabled, String userName);

    /**
     * 重置密码
     *
     * @param personId 人员id
     */
    void resetPassword(String personId);

    /**
     * 保存人员
     *
     * @param person 人员对象
     * @return {@link Y9Person}
     */
    Y9Person save(Y9Person person);

    /**
     * 保存人员头像
     *
     * @param personId 人员id
     * @param avatorUrl 头像路径
     * @return
     */
    Y9Person saveAvator(String personId, String avatorUrl);

    /**
     * 保存或更新
     *
     * @param person 人员对象
     * @param ext 人员扩展信息对象
     * @param positionIds 岗位id数组
     * @param jobIds 职位id数组
     * @return {@link Y9Person}
     */
    Y9Person saveOrUpdate(Y9Person person, Y9PersonExt ext, List<String> positionIds, List<String> jobIds);

    /**
     * 保存或者修改此岗位的信息
     *
     * @param person 人员对象
     * @param personExt 人员扩展信息对象
     * @return ORGPerson
     */
    Y9Person saveOrUpdate(Y9Person person, Y9PersonExt personExt);

    /**
     * 保存或者修改此人员的信息(用于导入y9导出的组织机构：密码是什么就导入什么不做处理)
     *
     * @param person 人员对象
     * @param personExt 人员扩展信息
     * @return ORGPerson
     */
    Y9Person saveOrUpdate4ImpOrg(Y9Person person, Y9PersonExt personExt);

    /**
     * 保存或者更新人员扩展信息
     *
     * @param personId 人员id
     * @param properties 扩展属性
     * @return {@link Y9Person}
     */
    Y9Person saveProperties(String personId, String properties);

    /**
     * 修改微信 id
     *
     * @param personId 人员 id
     * @param weixinId 微信 id
     */
    Y9Person saveWeixinId(String personId, String weixinId);

    /**
     * 根据where子句查询
     *
     * @param whereClause 查询子句
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> search(String whereClause);

    /**
     * 更新排序序列号
     *
     * @param id 人员id
     * @param tabIndex 排序序列号
     * @return {@link Y9Person}
     */
    Y9Person updateTabIndex(String id, int tabIndex);
}
