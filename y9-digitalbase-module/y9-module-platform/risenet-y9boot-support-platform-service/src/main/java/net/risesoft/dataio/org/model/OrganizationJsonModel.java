package net.risesoft.dataio.org.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.entity.org.Y9Organization;

/**
 * Y9Organization 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2025/02/08
 */
@Getter
@Setter
public class OrganizationJsonModel extends Y9Organization {

    private static final long serialVersionUID = 3570626067397227297L;

    // 基础数据
    private List<OptionClassJsonModel> optionClassList = new ArrayList<>();

    private List<JobJsonModel> jobList = new ArrayList<>();

    // 其他组织节点数据
    private List<DepartmentJsonModel> departmentList = new ArrayList<>();

    private List<GroupJsonModel> groupList = new ArrayList<>();

    private List<PersonJsonModel> personList = new ArrayList<>();

    private List<PositionJsonModel> positionList = new ArrayList<>();

}
