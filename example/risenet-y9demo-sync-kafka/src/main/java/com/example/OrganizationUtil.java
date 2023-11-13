package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Organization;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;

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
     * @param dataMap
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void syncOrganization(Map<String, Object> dataMap) {
        String syncId = (String)dataMap.get(Y9OrgEventConst.SYNC_ID);
        Integer syncRecursion = (Integer)dataMap.get(Y9OrgEventConst.SYNC_RECURSION);
        Organization org = (Organization)dataMap.get(syncId);
        boolean exist = checkOrgExist(org);
        if (exist) {// 执行相应操作
            // updateOrgznization(org);更新
        } else {
            // addOrgznization(org);新增
        }
        if (syncRecursion == 1) {// 递归同步,机构下的部门、人员
            List<Department> childDeptList = (ArrayList<Department>)dataMap.get(syncId + "Department");
            for (Department d : childDeptList) {
                DepartmentUtil.recursionDeptAndPersn(dataMap, d, d.getId());
            }

        }
    }

}
