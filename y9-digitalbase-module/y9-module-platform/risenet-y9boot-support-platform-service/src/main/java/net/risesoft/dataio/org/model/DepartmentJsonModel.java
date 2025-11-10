package net.risesoft.dataio.org.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.model.platform.org.Department;

/**
 * Y9Department 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2025/02/08
 */
@Getter
@Setter
public class DepartmentJsonModel extends Department {

    private static final long serialVersionUID = 1524447225535226416L;

    private List<DepartmentPropJsonModel> departmentPropList = new ArrayList<>();

    private List<DepartmentJsonModel> subDepartmentList = new ArrayList<>();

    private List<GroupJsonModel> groupList = new ArrayList<>();

    private List<PersonJsonModel> personList = new ArrayList<>();

    private List<PositionJsonModel> positionList = new ArrayList<>();

}
