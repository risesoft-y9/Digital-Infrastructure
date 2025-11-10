package net.risesoft.model.platform.permission.cache;

import lombok.Data;

/**
 * 人员与（资源、权限）关系表
 *
 * @author shidaobang
 * @date 2025/11/05
 */
@Data
public class PersonToResource extends IdentityToResourceBase {

    private static final long serialVersionUID = 612591201404990189L;

    /**
     * 身份(人员)唯一标识
     */
    private String personId;

}
