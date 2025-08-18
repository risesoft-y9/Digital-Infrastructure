package net.risesoft.dataio.org.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.entity.org.Y9Person;

/**
 * Y9Person 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2025/02/08
 */
@Getter
@Setter
public class Y9PersonJsonModel extends Y9Person {

    private static final long serialVersionUID = -3494909801125830498L;

    private Y9PersonExtJsonModel y9PersonExt;

    private List<Y9PersonsToGroupsJsonModel> personsToGroupsList = new ArrayList<>();

    private List<Y9PersonsToPositionsJsonModel> personsToPositionsList = new ArrayList<>();

}
