package net.risesoft.dataio.system.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9public.entity.resource.Y9Menu;

/**
 * Y9MenuDTO
 *
 * @author shidaobang
 * @date 2022/6/7
 */
@Getter
@Setter
public class Y9MenuExportModel extends Y9Menu {

    private static final long serialVersionUID = 6051765640925960718L;

    private List<Y9MenuExportModel> subMenuList;

    private List<Y9OperationExportModel> subOperationList;

}
