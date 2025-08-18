package com.example;

import java.util.List;
import java.util.stream.Collectors;

import net.risesoft.model.platform.SyncOrgUnits;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Group;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;

/**
 * 部门同步
 *
 * @author shidaobang
 * @date 2022/12/29
 */
public class DepartmentUtil {

    /**
     * 判断数据库中是否存在对应的部门
     *
     * @param d
     * @return
     */
    public static boolean checkDeptExist(Department d) {
        boolean exist = false;
        // 写代码
        return exist;
    }

    /**
     * 递归部门、人员、用户组及用户组人员、岗位及岗位人员
     *
     * @param syncOrgUnits
     * @param currentDept
     */
    public static void recursion(SyncOrgUnits syncOrgUnits, Department currentDept, String syncId) {
        if (checkDeptExist(currentDept)) {
            // updateDepartment(currentDept);写自己的更新代码
        } else {
            // addDepartment(currentDept);写自己的新增代码
        }

        // 部门下的人员
        List<Person> personList =
            syncOrgUnits.getPersons().stream().filter(p -> p.getParentId().equals(syncId)).collect(Collectors.toList());
        for (Person p : personList) {
            PersonUtil.syncPerson(p);
        }

        // 部门下岗位
        List<Position> positionList = syncOrgUnits.getPositions()
            .stream()
            .filter(p -> p.getParentId().equals(syncId))
            .collect(Collectors.toList());
        for (Position position : positionList) {
            PositionUtil.recursion(syncOrgUnits, position, position.getId());
        }

        // 部门下用户组
        List<Group> groupList =
            syncOrgUnits.getGroups().stream().filter(g -> g.getParentId().equals(syncId)).collect(Collectors.toList());
        for (Group group : groupList) {
            GroupUtil.recursion(syncOrgUnits, group, group.getId());
        }

        // 子部门
        List<Department> childDeptList = syncOrgUnits.getDepartments()
            .stream()
            .filter(d -> d.getParentId().equals(syncId))
            .collect(Collectors.toList());
        for (Department d : childDeptList) {
            recursion(syncOrgUnits, d, d.getId());
        }

    }

    /**
     * 同步部门
     *
     * @param syncOrgUnits
     */
    public static void syncDepartment(SyncOrgUnits syncOrgUnits) {
        String syncId = syncOrgUnits.getOrgUnitId();
        boolean needRecursion = syncOrgUnits.isNeedRecursion();
        Department currentDept =
            syncOrgUnits.getDepartments().stream().filter(d -> d.getId().equals(syncId)).findFirst().get();

        if (needRecursion) {
            recursion(syncOrgUnits, currentDept, syncId);
        } else {
            // 根据id判定部门是否存在，是为true
            if (checkDeptExist(currentDept)) {
                // updateDepartment(currentDept);写自己的更新代码
            } else {
                // addDepartment(currentDept);写自己的新增代码
            }
        }
    }

}
