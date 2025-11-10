package net.risesoft.model.platform.permission.cache;

import lombok.Data;

/**
 * 人员与角色关系
 *
 * @author shidaobang
 * @date 2025/11/05
 */
@Data
public class PersonToRole extends IdentityToRoleBase {

    private static final long serialVersionUID = -8979522859259684569L;

    /** 身份(人员)唯一标识 */
    private String personId;

}
