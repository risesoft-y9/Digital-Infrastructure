package net.risesoft.api.org;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.ManagerApi;
import net.risesoft.entity.Y9Manager;
import net.risesoft.model.platform.Manager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * 三员服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/manager", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ManagerApiImpl implements ManagerApi {

    private final Y9ManagerService y9ManagerService;

    /**
     * 根据id获得管理员对象
     *
     * @param tenantId 租户id
     * @param id 人员唯一标识
     * @return {@code Y9Result<Manager>} 通用请求返回对象 - data 是管理员对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Manager> getManagerById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("id") @NotBlank String id) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Manager y9Manager = y9ManagerService.findById(id).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Manager, Manager.class));
    }

    /**
     * 根据登录名获得管理员对象
     *
     * @param tenantId 租户id
     * @param loginName 登录名
     * @return {@code Y9Result<Manager>} 通用请求返回对象 - data 是管理员对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Manager> getManagerByLoginName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("loginName") @NotBlank String loginName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Manager y9Manager = y9ManagerService.findByLoginName(loginName).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Manager, Manager.class));
    }

    /**
     * 判断是否为部门的三员
     *
     * @param tenantId 租户id
     * @param managerId 管理员唯一标识
     * @param deptId 部门id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否为该部门的三员
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> isDeptManager(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("managerId") @NotBlank String managerId, @RequestParam("deptId") @NotBlank String deptId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9ManagerService.isDeptManager(managerId, deptId));
    }

}
