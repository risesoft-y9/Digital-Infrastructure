package net.risesoft.model.platform.permission.cache;

import lombok.Data;

/**
 * 岗位与角色关系
 *
 * @author shidaobang
 * @date 2025/11/05
 */
@Data
public class PositionToRole extends IdentityToRoleBase {

    private static final long serialVersionUID = 871609815185417568L;

    /** 身份(岗位)唯一标识 */
    private String positionId;

}
