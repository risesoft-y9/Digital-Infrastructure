package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色级别列举
 *
 * @author shidaobang
 * @date 2026/05/26
 */
@Getter
@AllArgsConstructor
public enum RoleLevelEnum {
    /**
     * 公共级，所有系统的资源都可以关联授权
     */
    PUBLIC("公共级"),
    /**
     * 系统级，系统下所有资源都可以关联授权
     */
    SYSTEM("系统级"),
    /**
     * 应用级，应用下所有资源都可以关联授权
     */
    APP("应用级");

    private final String name;

}
