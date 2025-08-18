package com.example;

import java.util.List;
import java.util.stream.Collectors;

import net.risesoft.model.platform.SyncOrgUnits;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Group;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;

/**
 * 组织机构同步
 *
 * @author shidaobang
 * @date 2022/12/29
 */
public class OrganizationUtil {

    /**
     * 判断数据库中是否存在对应的机构
     *
     * @param org
     * @return
     */
    public static boolean checkOrgExist(Organization org) {
        boolean exist = false;
        // 写代码
        return exist;
    }

    /**
     * 组织架构同步
     *
     * @param syncOrgUnits
     */
    public static void syncOrganization(SyncOrgUnits syncOrgUnits) {
        String syncId = syncOrgUnits.getOrgUnitId();
        boolean needRecursion = syncOrgUnits.isNeedRecursion();
        Organization org = syncOrgUnits.getOrganization();

        if (checkOrgExist(org)) {
            // updateOrgznization(org);更新
        } else {
            // addOrgznization(org);新增
        }

        if (needRecursion) {
            // 机构下的人员
            List<Person> personList = syncOrgUnits.getPersons()
                .stream()
                .filter(p -> p.getParentId().equals(syncId))
                .collect(Collectors.toList());
            for (Person p : personList) {
                PersonUtil.syncPerson(p);
            }

            // 机构下岗位
            List<Position> positionList = syncOrgUnits.getPositions()
                .stream()
                .filter(p -> p.getParentId().equals(syncId))
                .collect(Collectors.toList());
            for (Position position : positionList) {
                PositionUtil.recursion(syncOrgUnits, position, position.getId());
            }

            // 机构下用户组
            List<Group> groupList = syncOrgUnits.getGroups()
                .stream()
                .filter(g -> g.getParentId().equals(syncId))
                .collect(Collectors.toList());
            for (Group group : groupList) {
                GroupUtil.recursion(syncOrgUnits, group, group.getId());
            }

            // 机构下的部门
            List<Department> childDeptList = syncOrgUnits.getDepartments()
                .stream()
                .filter(d -> d.getParentId().equals(syncId))
                .collect(Collectors.toList());
            for (Department d : childDeptList) {
                DepartmentUtil.recursion(syncOrgUnits, d, d.getId());
            }

        }
    }

}
