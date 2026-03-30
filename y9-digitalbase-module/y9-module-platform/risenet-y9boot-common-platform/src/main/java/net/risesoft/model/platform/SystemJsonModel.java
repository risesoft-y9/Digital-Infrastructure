package net.risesoft.model.platform;

import java.util.List;

import jakarta.validation.Valid;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.model.platform.resource.AppJsonModel;

/**
 * 系统导出 JSON 模型
 *
 * @author shidaobang
 * @since 9.6.10
 */
@Getter
@Setter
public class SystemJsonModel extends System {

    private static final long serialVersionUID = -1010121704644942107L;

    @Valid
    private List<RoleJsonModel> subRoleList;

    @Valid
    private List<AppJsonModel> appList;
}
