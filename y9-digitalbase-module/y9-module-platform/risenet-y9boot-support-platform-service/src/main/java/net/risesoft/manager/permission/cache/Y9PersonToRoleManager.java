package net.risesoft.manager.permission.cache;

import java.util.List;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.y9public.entity.Y9Role;

/**
 * 人员角色缓存 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9PersonToRoleManager {
    /**
     * 根据人员id获取角色id列表
     *
     * @param personId 人员id
     * @return 角色id列表
     */
    List<String> findRoleIdByPersonId(String personId);

    /**
     * 删除人员角色缓存
     *
     * @param personId 人员id
     * @param roleId 角色id
     */
    void removeByPersonIdAndRoleId(String personId, String roleId);

    /**
     * 保存人员角色缓存
     *
     * @param person 人员信息
     * @param role 角色信息
     */
    void save(Y9Person person, Y9Role role);
}
