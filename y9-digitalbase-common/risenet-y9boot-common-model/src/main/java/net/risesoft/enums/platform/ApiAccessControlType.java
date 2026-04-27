package net.risesoft.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * api访问控制类型
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
@Getter
@AllArgsConstructor
public enum ApiAccessControlType {
    WHITE_LIST,
    BLACK_LIST,
    APP_ID_SECRET;
}
