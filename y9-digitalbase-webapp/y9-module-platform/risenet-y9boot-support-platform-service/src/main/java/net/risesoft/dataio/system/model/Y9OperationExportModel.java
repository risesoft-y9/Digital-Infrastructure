package net.risesoft.dataio.system.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9public.entity.resource.Y9Operation;

/**
 * Y9OperationDTO
 *
 * @author shidaobang
 * @date 2022/6/7
 */
@Getter
@Setter
public class Y9OperationExportModel extends Y9Operation {

    private static final long serialVersionUID = -7493930537365048851L;

    private List<Y9MenuExportModel> subMenuList;

    private List<Y9OperationExportModel> subOperationList;

}
