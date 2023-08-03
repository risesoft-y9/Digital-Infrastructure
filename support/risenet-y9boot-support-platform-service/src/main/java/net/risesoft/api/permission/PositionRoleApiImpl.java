package net.risesoft.api.permission;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.service.identity.Y9PositionToRoleService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 岗位角色接口实现类
 *
 * @author shidaobang
 * @date 2022/11/11
 * @since 9.6.0
 */
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/positionRole", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PositionRoleApiImpl implements PositionRoleApi {

    private final Y9PositionToRoleService y9PositionToRoleService;

    /**
     * 判断岗位是否拥有 customId 对应的角色
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param customId 自定义id
     * @return
     * @since 9.6.0
     */
    @Override
    public Boolean hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("customId") @NotBlank String customId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PositionToRoleService.hasRole(positionId, customId);
    }
}
