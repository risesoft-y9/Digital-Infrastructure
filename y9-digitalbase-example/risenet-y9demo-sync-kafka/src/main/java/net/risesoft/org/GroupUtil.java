package net.risesoft.org;

import java.util.List;
import java.util.stream.Collectors;

import net.risesoft.model.platform.SyncOrgUnits;
import net.risesoft.model.platform.org.Group;
import net.risesoft.model.platform.org.PersonsGroups;

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
     * @param syncOrgUnits
     */
    public static void syncGroup(SyncOrgUnits syncOrgUnits) {
        String syncId = syncOrgUnits.getOrgUnitId();
        boolean needRecursion = syncOrgUnits.isNeedRecursion();
        Group g = syncOrgUnits.getGroups().get(0);

        if (needRecursion) {
            recursion(syncOrgUnits, g, syncId);
        } else {

            if (checkGroupExist(g)) {
                // updateGroup(g);
            } else {
                // addGroup(g);
            }

        }
    }

    public static void recursion(SyncOrgUnits syncOrgUnits, Group g, String syncId) {
        if (checkGroupExist(g)) {
            // updateGroup(g);
        } else {
            // addGroup(g);
        }

        // 递归，获取用户组中的人员，保存用户组人员绑定关系
        List<PersonsGroups> pgList = syncOrgUnits.getPersonsGroups()
            .stream()
            .filter(pg -> pg.getGroupId().equals(syncId))
            .collect(Collectors.toList());
        for (PersonsGroups pg : pgList) {
            PersonsGroupsUtil.addPersonsGroup(pg);
        }
    }
}
