package net.risesoft.dataio.resource.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9public.entity.resource.Y9Operation;

/**
 * Y9Operation 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2022/6/7
 */
@Getter
@Setter
public class OperationJsonModel extends Y9Operation {

    private static final long serialVersionUID = -7493930537365048851L;

    private List<MenuJsonModel> subMenuList;

    private List<OperationJsonModel> subOperationList;

}
