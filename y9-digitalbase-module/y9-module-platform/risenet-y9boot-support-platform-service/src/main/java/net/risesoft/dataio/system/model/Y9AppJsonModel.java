package net.risesoft.dataio.system.model;

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
public class Y9AppJsonModel extends Y9App {

    private static final long serialVersionUID = -6368600177705586612L;

    private List<Y9MenuJsonModel> subMenuList;

    private List<Y9OperationJsonModel> subOperationList;

    private List<Y9RoleJsonModel> subRoleList;

}
