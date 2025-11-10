package net.risesoft.dataio.system.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9public.entity.resource.Y9Menu;

/**
 * Y9Menu 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2022/6/7
 */
@Getter
@Setter
public class MenuJsonModel extends Y9Menu {

    private static final long serialVersionUID = 6051765640925960718L;

    private List<MenuJsonModel> subMenuList;

    private List<OperationJsonModel> subOperationList;

}
