package net.risesoft.service.identity;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.entity.role.Y9Role;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PersonToRoleService {

    /**
     * 重新计算人员的角色
     *
     * @param personId 人员id
     */
    void recalculate(String personId);

    /**
     * 根据人员id对个人授权计数
     *
     * @param personId
     * @return
     */
    long countByPersonId(String personId);

    /**
     * 根据人员id及系统名对个人授权计数
     *
     * @param personId
     * @param systemName
     * @return
     */
    long countByPersonIdAndSystemName(String personId, String systemName);

    /**
     * 人员是否拥有customId对应的角色
     * 
     * @param personId
     * @param customId
     * @return
     */
    Boolean hasRole(String personId, String customId);

    /**
     * 根据人员id，查询个人授权列表
     *
     * @param personId
     * @return
     */
    List<Y9PersonToRole> listByPersonId(String personId);
    
    /**
     * 根据人员id删除
     *
     * @param personId
     */
    void removeByPersonId(String personId);

    /**
     * 根据角色id移除
     *
     * @param roleId
     */
    void removeByRoleId(String roleId);

    /**
     * 更新人员授权信息
     *
     * @param roleId
     * @param roleName
     * @param systemName
     * @param systemCnName
     * @param description
     */
    void update(String roleId, String roleName, String systemName, String systemCnName, String description);

    /**
     * 根据角色更新
     *
     * @param y9Role
     * @return
     */
    void updateByRole(Y9Role y9Role);

    /**
     * 根据人员id获取拥有的角色id（,分隔）
     *
     * @param personId id
     * @return {@link String}
     */
    String getRoleIdsByPersonId(String personId);
}
