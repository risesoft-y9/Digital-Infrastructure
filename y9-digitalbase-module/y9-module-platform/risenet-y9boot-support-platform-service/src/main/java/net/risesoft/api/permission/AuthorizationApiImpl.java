package net.risesoft.api.permission;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.permission.AuthorizationApi;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.permission.Authorization;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.permission.Y9AuthorizationService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 权限管理组件
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
@RequestMapping(value = "/services/rest/v1/authorization", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthorizationApiImpl implements AuthorizationApi {

    private final Y9AuthorizationService y9AuthorizationService;
    private final Y9PersonService y9PersonService;

    /**
     * 保存授权信息
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param resourceId 资源id
     * @param roleId 角色id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return {@code Y9Result<Object>}
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> save(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("resourceId") @NotBlank String resourceId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("authority") AuthorityEnum authority) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Person person = y9PersonService.getById(personId);
        Authorization authorization = new Authorization();
        authorization.setAuthorizer(person.getName());
        authorization.setAuthority(authority);
        authorization.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        authorization.setResourceId(resourceId);
        authorization.setPrincipalId(roleId);
        authorization.setTenantId(tenantId);

        y9AuthorizationService.saveOrUpdateRole(authorization);
        return Y9Result.success();
    }

}
