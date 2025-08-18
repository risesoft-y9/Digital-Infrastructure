package net.risesoft.dataio.org.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.entity.org.Y9Department;

/**
 * Y9Department 导出 JSON 模型
 *
 * @author shidaobang
 * @date 2025/02/08
 */
@Getter
@Setter
public class Y9DepartmentJsonModel extends Y9Department {

    private static final long serialVersionUID = 1524447225535226416L;

    private List<Y9DepartmentPropJsonModel> departmentPropList = new ArrayList<>();

    private List<Y9DepartmentJsonModel> subDepartmentList = new ArrayList<>();

    private List<Y9GroupJsonModel> groupList = new ArrayList<>();

    private List<Y9PersonJsonModel> personList = new ArrayList<>();

    private List<Y9PositionJsonModel> positionList = new ArrayList<>();

}
