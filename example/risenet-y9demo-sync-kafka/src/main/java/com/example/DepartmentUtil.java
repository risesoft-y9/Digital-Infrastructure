package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Person;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;

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
     * @param dataMap
     * @param currentDept
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void recursionDeptAndPersn(Map<String, Object> dataMap, Department currentDept, String syncId) {
        if (checkDeptExist(currentDept)) {
            // updateDepartment(currentDept);写自己的更新代码
        } else {
            // addDepartment(currentDept);写自己的新增代码
        }
        // 部门下的人员
        List<Person> personList = (List<Person>)dataMap.get(syncId + "Person");
        for (Person p : personList) {
            try {
                PersonUtil.syncPerson(p);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        // 子部门
        List<Department> childDeptList = (ArrayList<Department>)dataMap.get(syncId + "Department");
        for (Department d : childDeptList) {
            recursionDeptAndPersn(dataMap, d, d.getId());
        }

    }

    /**
     * 同步部门
     *
     * @param dataMap
     * @throws Exception
     */
    public static void syncDepartment(Map<String, Object> dataMap) {
        String syncId = (String)dataMap.get(Y9OrgEventConst.SYNC_ID);
        Integer syncRecursion = (Integer)dataMap.get(Y9OrgEventConst.SYNC_RECURSION);
        Department currentDept = (Department)dataMap.get(syncId);
        if (syncRecursion == 0) {
            boolean exist = checkDeptExist(currentDept);// 根据id判定部门是否存在，是为true
            if (exist) {// 执行相应操作
                // updateDepartment(currentDept);写自己的更新代码
            } else {
                // addDepartment(currentDept);写自己的新增代码
            }
        } else if (syncRecursion == 1) {
            // deleteDepartment(currentDept);写自己的删除代码
            recursionDeptAndPersn(dataMap, currentDept, syncId);
        }
    }

}
