package net.risesoft.dataio.org.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.entity.Y9Position;

/**
 * Y9Position 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2025/02/08
 */
@Getter
@Setter
public class Y9PositionJsonModel extends Y9Position {

    private static final long serialVersionUID = 1148341725687417599L;

    private List<Y9PersonsToPositionsJsonModel> personsToPositionsList = new ArrayList<>();

}
