package net.risesoft.service.permission.cache;

/**
 * 身份角色计算器
 *
 * @author shidaobang
 * @date 2024/03/08
 */
public interface IdentityRoleCalculator {

    /**
     * 根据组织节点id重新计算身份角色
     *
     * @param orgUnitId 组织节点id
     */
    void recalculateByOrgUnitId(String orgUnitId);

    /**
     * 根据人员id重新计算身份角色
     *
     * @param personId 人员id
     */
    void recalculateByPersonId(String personId);

}
