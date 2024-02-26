package net.risesoft.api.platform.v0.org;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.platform.Manager;

/**
 * 三员服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
@Deprecated
public interface ManagerApi {

    /**
     * 根据id获得人员对象
     *
     * @param tenantId 租户id
     * @param userId 人员唯一标识
     * @return Manager 人员对象
     * @since 9.6.0
     */
    @GetMapping("/getManager")
    Manager getManagerById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("id") @NotBlank String userId);

    /**
     * 根据登录名获得人员对象
     *
     * @param tenantId 租户id
     * @param loginName 登录名
     * @return Manager 人员对象
     * @since 9.6.0
     */
    @GetMapping("/getManagerByLoginName")
    Manager getManagerByLoginName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("loginName") @NotBlank String loginName);

    /**
     * 判断是否为该部门的三员
     *
     * @param tenantId 租户id
     * @param managerId 人员唯一标识
     * @param deptId 三员唯一标识
     * @return boolean 是否为该部门的三员
     * @since 9.6.0
     */
    @GetMapping("/isDeptManager")
    boolean isDeptManager(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("managerId") @NotBlank String managerId, @RequestParam("deptId") @NotBlank String deptId);
}
