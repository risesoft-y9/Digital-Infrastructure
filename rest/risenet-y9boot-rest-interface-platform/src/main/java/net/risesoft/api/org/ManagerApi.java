package net.risesoft.api.org;

import net.risesoft.model.Manager;

/**
 * 三员服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface ManagerApi {

    /**
     * 根据id获得人员对象
     *
     * @param tenantId 租户id
     * @param userId 人员唯一标识
     * @return Manager 人员对象
     * @since 9.6.0
     */
    Manager getManager(String tenantId, String userId);

    /**
     * 判断是否为该部门的三员
     *
     * @param tenantId 租户id
     * @param managerId 三员唯一标识
     * @param deptId 部门唯一标识
     * @return boolean 是否为该部门的三员
     * @since 9.6.0
     */
    boolean isDeptManager(String tenantId, String managerId, String deptId);

}
