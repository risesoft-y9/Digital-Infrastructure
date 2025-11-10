package net.risesoft.model.platform.permission.cache;

import lombok.Data;

/**
 * 岗位与（资源、权限）关系表
 *
 * @author shidaobang
 * @date 2025/11/05
 */
@Data
public class PositionToResource extends IdentityToResourceBase {

    private static final long serialVersionUID = -8527781135976550912L;

    /** 身份(岗位)唯一标识 */
    private String positionId;

}
