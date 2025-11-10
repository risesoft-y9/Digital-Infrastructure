package net.risesoft.model.platform.permission.cache;

import lombok.Data;

import net.risesoft.model.BaseModel;

/**
 * 人员/岗位拥有的角色基类
 *
 * @author shidaobang
 * @date 2025/11/05
 */
@Data
public class IdentityToRoleBase extends BaseModel {

    private static final long serialVersionUID = -573545238495850212L;

    /** id */
    protected String id;

    /** 租户id */
    protected String tenantId;

    /** 角色id */
    protected String roleId;

    /** 应用id */
    protected String appId;

    /** 系统id */
    protected String systemId;

}
