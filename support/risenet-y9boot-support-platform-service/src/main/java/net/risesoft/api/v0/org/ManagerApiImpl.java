package net.risesoft.api.v0.org;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Manager;
import net.risesoft.model.Manager;
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
@RestController(value = "v0ManagerApiImpl")
@RequestMapping(value = "/services/rest/manager", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class ManagerApiImpl implements ManagerApi {

    private final Y9ManagerService y9ManagerService;

    /**
     * 根据id获得人员对象
     *
     * @param tenantId 租户id
     * @param userId 人员唯一标识
     * @return Manager 人员对象
     * @since 9.6.0
     */
    @Override
    public Manager getManagerById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("userId") @NotBlank String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Manager y9Manager = y9ManagerService.findById(userId).orElse(null);
        return Y9ModelConvertUtil.convert(y9Manager, Manager.class);
    }

    @Override
    public Manager getManagerByLoginName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("loginName") @NotBlank String loginName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Manager y9Manager = y9ManagerService.findByLoginName(loginName).orElse(null);
        return Y9ModelConvertUtil.convert(y9Manager, Manager.class);
    }

    /**
     * 判断是否为该部门的三员
     *
     * @param tenantId 租户id
     * @param managerId 人员唯一标识
     * @param deptId 三员唯一标识
     * @return boolean 是否为该部门的三员
     * @since 9.6.0
     */
    @Override
    public boolean isDeptManager(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("managerId") @NotBlank String managerId, @RequestParam("deptId") @NotBlank String deptId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9ManagerService.isDeptManager(managerId, deptId);
    }

}
