package net.risesoft.api.permission;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.AuthorityEnum;
import net.risesoft.pojo.Y9Result;

/**
 * 权限管理组件
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface AuthorizationApi {

    /**
     * 保存授权信息
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param resourceId 资源id
     * @param roleId 角色id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/save")
    Y9Result<Object> save(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("resourceId") @NotBlank String resourceId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("authority") Integer authority);

}
