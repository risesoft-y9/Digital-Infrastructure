package net.risesoft.dataio.org.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.entity.Y9Organization;

/**
 * Y9Organization 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2025/02/08
 */
@Getter
@Setter
public class Y9OrganizationJsonModel extends Y9Organization {

    private static final long serialVersionUID = 3570626067397227297L;

    // 基础数据
    private List<Y9OptionClassJsonModel> optionClassList = new ArrayList<>();

    private List<Y9JobJsonModel> jobList = new ArrayList<>();

    // 其他组织节点数据
    private List<Y9DepartmentJsonModel> departmentList = new ArrayList<>();

    private List<Y9GroupJsonModel> groupList = new ArrayList<>();

    private List<Y9PersonJsonModel> personList = new ArrayList<>();

    private List<Y9PositionJsonModel> positionList = new ArrayList<>();

}
