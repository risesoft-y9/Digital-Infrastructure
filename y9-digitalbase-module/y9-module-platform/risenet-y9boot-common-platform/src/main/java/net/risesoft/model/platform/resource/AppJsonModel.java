package net.risesoft.model.platform.resource;

import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import net.risesoft.model.platform.RoleJsonModel;

/**
 * 应用导出 JSON 模型
 *
 * @author shidaobang
 * @since 9.6.10
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public class AppJsonModel extends App {

    private static final long serialVersionUID = -6368600177705586612L;

    @Valid
    private List<MenuJsonModel> subMenuList;

    @Valid
    private List<OperationJsonModel> subOperationList;

    @Valid
    private List<RoleJsonModel> subRoleList;
}
