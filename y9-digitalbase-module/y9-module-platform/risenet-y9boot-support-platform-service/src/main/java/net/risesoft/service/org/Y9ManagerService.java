package net.risesoft.service.org;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import net.risesoft.entity.org.Y9Manager;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
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
     * @return {@code Optional<Y9Manager>} 管理员对象 或 null
     */
    Optional<Y9Manager> findById(String id);

    Optional<Y9Manager> findByLoginName(String loginName);

    /**
     * 根据id获取管理员信息
     *
     * @param id 管理员id
     * @return Y9Manager 管理员对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Manager getById(String id);

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
     * @return {@code List<Y9Manager}
     */
    List<Y9Manager> listAll();

    /**
     * 获取管理员列表
     *
     * @param globalManager 是否全局管理员
     * @return {@code List<Y9Manager>}
     */
    List<Y9Manager> listByGlobalManager(boolean globalManager);

    /**
     * 根据父节点id获取管理员列表
     *
     * @param parentId 父节点id
     * @return {@code List<Y9Manager>}
     */
    List<Y9Manager> listByParentId(String parentId);

    /**
     * 重置为默认密码
     *
     * @param id 管理员id
     * @return {@link Y9Manager}
     */
    Y9Manager resetDefaultPassword(String id);

    /**
     * 保存管理员信息
     *
     * @param y9Manager 管理员对象
     * @return {@link Y9Manager}
     */
    Y9Manager saveOrUpdate(Y9Manager y9Manager);

    /**
     * 更新检查时间
     *
     * @param managerId 管理员id
     * @param checkTime 审查时间
     */
    void updateCheckTime(String managerId, Date checkTime);
}
