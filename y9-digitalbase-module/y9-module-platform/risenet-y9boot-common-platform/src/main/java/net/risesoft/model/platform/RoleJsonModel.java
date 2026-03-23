package net.risesoft.model.platform;

import java.util.List;

import javax.validation.Valid;

import lombok.Getter;
import lombok.Setter;

/**
 * 角色导出 JSON 模型
 *
 * @author shidaobang
 * @since 9.6.10
 */
@Getter
@Setter
public class RoleJsonModel extends Role {

    private static final long serialVersionUID = 5734021385079857067L;

    @Valid
    private List<RoleJsonModel> subRoleList;
}
