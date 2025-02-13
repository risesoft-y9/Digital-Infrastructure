package net.risesoft.api.platform.user;

import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;

/**
 * 账号组件
 *
 * @author shidaobang
 * @date 2025/02/13
 * @since 9.6.8
 */
public interface UserApi {

    /**
     * 根据id获得账号对象
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return {@code Y9Result<UserInfo>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.8
     */
    @GetMapping("/get")
    Y9Result<UserInfo> get(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

}
