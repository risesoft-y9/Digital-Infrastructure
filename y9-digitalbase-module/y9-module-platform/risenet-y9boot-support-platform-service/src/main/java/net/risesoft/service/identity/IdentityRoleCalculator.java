package net.risesoft.service.identity;

/**
 * 身份角色计算器
 *
 * @author shidaobang
 * @date 2024/03/08
 */
public interface IdentityRoleCalculator {
    void recalculateByOrgUnitId(String orgUnitId);

    void recalculateByPersonId(String personId);
}
