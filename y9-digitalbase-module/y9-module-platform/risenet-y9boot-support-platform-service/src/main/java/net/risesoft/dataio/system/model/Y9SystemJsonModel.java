package net.risesoft.dataio.system.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9public.entity.resource.Y9System;

/**
 * Y9System 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2022/6/7
 */
@Getter
@Setter
public class Y9SystemJsonModel extends Y9System {

    private static final long serialVersionUID = -1010121704644942107L;

    private List<Y9AppJsonModel> appList;
}
