package net.risesoft.model.platform;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Group;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.PersonsGroups;
import net.risesoft.model.platform.org.PersonsPositions;
import net.risesoft.model.platform.org.Position;

/**
 * 同步组织架构返回的数据结构
 *
 * @author shidaobang
 * @date 2023/11/24
 * @since 9.6.3
 */
@SuppressWarnings("serial")
@Data
public class SyncOrgUnits implements Serializable {

    @Serial
    private static final long serialVersionUID = -864558419670526377L;

    /** 同步的组织节点id */
    private String orgUnitId;
    /** 同步的组织节点类型 */
    private OrgTypeEnum orgTypeEnum;
    /** 同步的组织节点是否有递归 */
    private boolean needRecursion;

    /** 组织机构 */
    private Organization organization = null;
    /** 部门 */
    private List<Department> departments = new ArrayList<>();
    /** 人员 */
    private List<Person> persons = new ArrayList<>();
    /** 岗位 */
    private List<Position> positions = new ArrayList<>();
    /** 用户组 */
    private List<Group> groups = new ArrayList<>();
    /** 人员和岗位的绑定关系 */
    private List<PersonsPositions> personsPositions = new ArrayList<>();
    /** 人员和用户组的绑定关系 */
    private List<PersonsGroups> personsGroups = new ArrayList<>();

}
