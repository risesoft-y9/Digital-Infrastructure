package net.risesoft.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 应用审核状态
 *
 * @author shidaobang
 * @date 2023/10/08
 * @since 9.6.3
 */
@RequiredArgsConstructor
@Getter
public enum AppVerifyStatusEnum {
    /** 未审核 */
    NOT_REVIEWED(0, "未审核"),
    /** 未通过 */
    REJECTED(1, "未通过"),
    /** 通过 */
    APPROVED(2, "通过");

    private final Integer value;
    private final String name;
}
