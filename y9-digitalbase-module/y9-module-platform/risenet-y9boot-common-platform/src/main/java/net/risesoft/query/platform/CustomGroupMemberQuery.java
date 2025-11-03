package net.risesoft.query.platform;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.OrgTypeEnum;

/**
 * 自定义用户组成员查询
 *
 * @author shidaobang
 * @date 2025/10/27
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CustomGroupMemberQuery {

    /**
     * 用户组 id
     */
    @NotBlank
    private final String groupId;

    /**
     * 成员类型
     */
    private OrgTypeEnum memberType;
}
