package net.risesoft.service.permission.cache;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;

/**
 * 身份资源计算器
 *
 * @author shidaobang
 * @date 2024/03/08
 */
public interface IdentityResourceCalculator {

    /**
     * 同步orgUnitId相关的权限配置至身份（人员或者岗位）对资源的权限缓存表
     *
     * @param orgUnitId 组织节点id
     */
    void recalculateByOrgUnitId(String orgUnitId);

    /**
     * 同步人员相关的权限配置至人员对资源的权限缓存表
     *
     * @param person 人员对象
     */
    void recalculateByPerson(Y9Person person);

    /**
     * 同步岗位相关的权限配置至岗位对资源的权限缓存表
     *
     * @param position 岗位对象
     */
    void recalculateByPosition(Y9Position position);

    /**
     * 获取资源id相关的权限配置同步至权限缓存表
     *
     * @param resourceId 资源id
     */
    void recalculateByResourceId(String resourceId);
}
