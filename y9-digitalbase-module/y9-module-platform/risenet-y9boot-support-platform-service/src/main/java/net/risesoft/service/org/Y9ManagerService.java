package net.risesoft.service.org;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.model.platform.org.Manager;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9ManagerService {

    /**
     * 根据管理员id，改变管理员禁用状态
     *
     * @param id 管理员唯一标识
     * @return {@link Manager}
     */
    Manager changeDisabled(String id);

    /**
     * 更改密码
     *
     * @param id id
     * @param newPassword 新密码
     */
    void changePassword(String id, String newPassword);

    /**
     * 检查密码是否正确
     *
     * @param personId 人员ID
     * @param password 密码
     * @return boolean
     */
    boolean checkPassword(String personId, String password);

    /**
     * 根据管理员id数组删除管理员信息
     *
     * @param ids 管理员id数组
     */
    void delete(List<String> ids);

    /**
     * 根据管理员id删除管理员信息
     *
     * @param id 管理员id
     */
    void delete(String id);

    /**
     * 根据id判断管理员是否存在
     *
     * @param id 管理员id
     * @return boolean
     */
    boolean existsById(String id);

    /**
     * 根据登录名判断管理员是否存在
     *
     * @param loginName 管理员id
     * @return boolean
     */
    boolean existsByLoginName(String loginName);

    /**
     * 根据id查找管理员
     *
     * @param id 管理员id
     * @return {@code Optional<Manager>} 管理员对象 或 null
     */
    Optional<Manager> findById(String id);

    Optional<Manager> findByLoginName(String loginName);

    /**
     * 根据id获取管理员信息
     *
     * @param id 管理员id
     * @return Manager 管理员对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Manager getById(String id);

    int getPasswordModifiedCycle(ManagerLevelEnum managerLevel);

    int getReviewLogCycle(ManagerLevelEnum managerLevel);

    /**
     * 是否为子域三员
     *
     * @param managerId 经理id
     * @param deptId 部门id
     * @return boolean
     */
    boolean isDeptManager(String managerId, String deptId);

    /**
     * 判断管理员登录名
     *
     * @param id 管理员id
     * @param loginName 登录名称
     * @return boolean
     */
    boolean isLoginNameAvailable(String id, String loginName);

    Boolean isPasswordExpired(String id);

    /**
     * 查询所有管理员
     *
     * @return {@code List<Manager}
     */
    List<Manager> listAll();

    /**
     * 获取管理员列表
     *
     * @param globalManager 是否全局管理员
     * @return {@code List<Manager>}
     */
    List<Manager> listByGlobalManager(boolean globalManager);

    /**
     * 根据父节点id获取管理员列表
     *
     * @param parentId 父节点id
     * @return {@code List<Manager>}
     */
    List<Manager> listByParentId(String parentId);

    /**
     * 重置为默认密码
     *
     * @param id 管理员id
     * @return {@link Manager}
     */
    Manager resetDefaultPassword(String id);

    /**
     * 保存管理员信息
     *
     * @param manager 管理员对象
     * @return {@link Manager}
     */
    Manager saveOrUpdate(Manager manager);

    /**
     * 更新检查时间
     *
     * @param managerId 管理员id
     * @param checkTime 审查时间
     */
    void updateCheckTime(String managerId, Date checkTime);

    /**
     * 根据所给的组织节点 id 列表过滤得到子域三员可管理的组织节点列表
     *
     * @param managerParentId 子域三员父节点 id
     * @param orgUnitIdList 组织节点 id 列表
     * @return {@code List<OrgUnit> }
     */
    List<OrgUnit> filterManagableOrgUnitList(String managerParentId, List<String> orgUnitIdList);
}
