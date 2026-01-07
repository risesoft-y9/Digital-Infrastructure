package net.risesoft.dataio.resource.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9public.entity.resource.Y9App;

/**
 * Y9App 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2022/6/7
 */
@Getter
@Setter
public class AppJsonModel extends Y9App {

    private static final long serialVersionUID = -6368600177705586612L;

    private List<MenuJsonModel> subMenuList;

    private List<OperationJsonModel> subOperationList;

    private List<RoleJsonModel> subRoleList;

}
