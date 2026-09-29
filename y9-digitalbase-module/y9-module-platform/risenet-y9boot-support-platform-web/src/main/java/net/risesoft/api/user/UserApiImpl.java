package net.risesoft.api.user;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.user.UserApi;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.service.user.Y9UserService;

/**
 * 账号组件
 *
 * @author shidaobang
 * @date 2025/02/13
 * @since 9.6.8
 */
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserApiImpl implements UserApi {

    private final Y9UserService y9UserService;

    /**
     * 根据id获得账号对象
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return {@code Y9Result<UserInfo>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.8
     */
    @Override
    public Y9Result<UserInfo> get(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        UserInfo userInfo = y9UserService.findByPersonIdAndTenantId(personId, tenantId).orElse(null);
        return Y9Result.success(userInfo);
    }
}
