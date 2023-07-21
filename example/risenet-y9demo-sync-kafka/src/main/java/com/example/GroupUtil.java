package com.example;

import java.util.List;
import java.util.Map;

import net.risesoft.model.Group;
import net.risesoft.model.PersonsGroups;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;

/**
 * 用户组同步
 *
 * @author shidaobang
 * @date 2022/12/29
 */
public class GroupUtil {

    /**
     * 检查用户组是否存在
     *
     * @param g
     * @return
     */
    public static boolean checkGroupExist(Group g) {
        boolean exist = false;
        return exist;
    }

    /**
     * 同步用户组信息
     *
     * @param dataMap
     */
    @SuppressWarnings("unchecked")
    public static void syncGroup(Map<String, Object> dataMap) {
        String syncId = (String)dataMap.get(Y9OrgEventConst.SYNC_ID);
        Integer syncRecursion = (Integer)dataMap.get(Y9OrgEventConst.SYNC_RECURSION);
        Group g = (Group)dataMap.get(syncId);
        boolean exist = checkGroupExist(g);
        if (exist) {
            // updateGroup(g);
        } else {
            // addGroup(g);
        }
        /**
         * 递归，获取用户组中的人员，保存用户组人员绑定关系
         */
        if (syncRecursion == 1) {
            List<PersonsGroups> pgList = (List<PersonsGroups>)dataMap.get(syncId + "PersonsGroups");
            for (PersonsGroups pg : pgList) {
                PersonsGroupsUtil.addPersonsGroup(pg);
            }
        }
    }
}
