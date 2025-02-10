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
public class Y9MenuJsonModel extends Y9Menu {

    private static final long serialVersionUID = 6051765640925960718L;

    private List<Y9MenuJsonModel> subMenuList;

    private List<Y9OperationJsonModel> subOperationList;

}
