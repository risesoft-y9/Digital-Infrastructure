package net.risesoft.service.org;

import java.util.List;

import net.risesoft.entity.Y9Manager;
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
     * @return {@link Y9Manager}
     */
    Y9Manager changeDisabled(String id);

    /**
     * 判断管理员登录名
     *
     * @param id        管理员id
     * @param loginName 登录名称
     * @return boolean
     */
    boolean checkLoginName(String id, String loginName);

    /**
     * 初始化安全审计员
     *
     * @param id 管理员id
     * @param tenantId 租户id
     * @param organizationId 组织id
     */
    void createAuditManager(String id, String tenantId, String organizationId);

    /**
     * 初始化安全保密员
     *
     * @param id 管理员id
     * @param tenantId 租户id
     * @param organizationId 组织id
     */
    void createSecurityManager(String id, String tenantId, String organizationId);

    /**
     * 初始化系统管理员
     *
     * @param managerId 管理员id
     * @param tenantId 租户id
     * @param organizationId 组织id
     */
    void createSystemManager(String managerId, String tenantId, String organizationId);

    /**
     * 根据管理员id删除管理员信息
     *
     * @param id 管理员id
     */
    void delete(String id);

    /**
     * 根据管理员id数组删除管理员信息
     *
     * @param ids 管理员id数组
     */
    void delete(String[] ids);

    /**
     * 根据id判断管理员是否存在
     *
     * @param id 管理员id
     * @return boolean
     */
    boolean existsById(String id);

    /**
     * 根据id查找管理员
     *
     * @param id 管理员id
     * @return 管理员对象 或 null
     */
    Y9Manager findById(String id);

    /**
     * 根据id获取管理员信息
     *
     * @param id 管理员id
     * @return 管理员对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Manager getById(String id);

    /**
     * 查询所有管理员
     *
     * @return {@link List}<{@link Y9Manager}>
     */
    List<Y9Manager> listAll();

    /**
     * 获取管理员列表
     *
     * @param globalManager 是否全局管理员
     * @return {@link List}<{@link Y9Manager}>
     */
    List<Y9Manager> listByGlobalManager(boolean globalManager);

    /**
     * 根据父节点id获取管理员列表
     *
     * @param parentId 父节点id
     * @return {@link List}<{@link Y9Manager}>
     */
    List<Y9Manager> listByParentId(String parentId);

    /**
     * 重置管理员密码
     *
     * @param id 管理员id
     * @return {@link Y9Manager}
     */
    Y9Manager resetPassword(String id);

    /**
     * 密码重置为默认
     *
     * @param id 管理员id
     */
    Y9Manager resetPasswordToDefault(String id);

    /**
     * 保存管理员信息
     *
     * @param y9Manager 管理员对象
     * @return {@link Y9Manager}
     */
    Y9Manager saveOrUpdate(Y9Manager y9Manager);

    /**
     * 更改密码
     *
     * @param id          id
     * @param newPassword 新密码
     */
    void changePassword(String id, String newPassword);

    /**
     * 是否为子域三员
     *
     * @param managerId 经理id
     * @param deptId    部门id
     * @return boolean
     */
    boolean isDeptManager(String managerId, String deptId);
}
