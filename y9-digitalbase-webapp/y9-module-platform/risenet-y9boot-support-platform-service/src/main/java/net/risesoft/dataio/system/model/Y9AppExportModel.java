package net.risesoft.dataio.system.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9public.entity.resource.Y9App;

/**
 * Y9AppDTO
 *
 * @author shidaobang
 * @date 2022/6/7
 */
@Getter
@Setter
public class Y9AppExportModel extends Y9App {

    private static final long serialVersionUID = -6368600177705586612L;

    private List<Y9MenuExportModel> subMenuList;

    private List<Y9OperationExportModel> subOperationList;

    private List<Y9RoleExportModel> subRoleList;

}
