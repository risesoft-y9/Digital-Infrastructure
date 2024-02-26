package net.risesoft.api.platform.org;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.platform.Manager;
import net.risesoft.pojo.Y9Result;

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
public interface ManagerApi {

    /**
     * 根据id获得管理员对象
     *
     * @param tenantId 租户id
     * @param id 管理员唯一标识
     * @return {@code Y9Result<Manager>} 通用请求返回对象 - data 是管理员对象
     * @since 9.6.0
     */
    @GetMapping("/getManager")
    Y9Result<Manager> getManagerById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("id") @NotBlank String id);

    /**
     * 根据登录名获得管理员对象
     *
     * @param tenantId 租户id
     * @param loginName 登录名
     * @return {@code Y9Result<Manager>} 通用请求返回对象 - data 是管理员对象
     * @since 9.6.0
     */
    @GetMapping("/getManagerByLoginName")
    Y9Result<Manager> getManagerByLoginName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("loginName") @NotBlank String loginName);

    /**
     * 判断是否为部门的三员
     *
     * @param tenantId 租户id
     * @param managerId 管理员唯一标识
     * @param deptId 部门id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否为该部门的三员
     * @since 9.6.0
     */
    @GetMapping("/isDeptManager")
    Y9Result<Boolean> isDeptManager(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("managerId") @NotBlank String managerId, @RequestParam("deptId") @NotBlank String deptId);
}
