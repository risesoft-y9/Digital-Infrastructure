package com.example;

import java.util.List;
import java.util.stream.Collectors;

import net.risesoft.model.platform.SyncOrgUnits;
import net.risesoft.model.platform.org.PersonsPositions;
import net.risesoft.model.platform.org.Position;

/**
 * 岗位同步
 *
 * @author shidaobang
 * @date 2022/12/29
 */
public class PositionUtil {
    /**
     * 判断岗位是否存在
     *
     * @param p
     * @return
     */
    public static boolean checkPositionExist(Position p) {
        boolean exist = false;
        return exist;
    }

    /**
     * 同步岗位
     *
     * @param syncOrgUnits
     */
    public static void syncPosition(SyncOrgUnits syncOrgUnits) {
        String syncId = syncOrgUnits.getOrgUnitId();
        boolean needRecursion = syncOrgUnits.isNeedRecursion();
        Position p = syncOrgUnits.getPositions().get(0);

        if (needRecursion) {
            recursion(syncOrgUnits, p, syncId);
        } else {

            if (checkPositionExist(p)) {
                // updatePosition(p);
            } else {
                // addPosition(p);
            }
        }
    }

    public static void recursion(SyncOrgUnits syncOrgUnits, Position p, String syncId) {

        if (checkPositionExist(p)) {
            // updatePosition(p);
        } else {
            // addPosition(p);
        }

        // 递归，获取岗位中的人员，保存岗位人员绑定关系
        List<PersonsPositions> ppList = syncOrgUnits.getPersonsPositions()
            .stream()
            .filter(pp -> pp.getPositionId().equals(syncId))
            .collect(Collectors.toList());
        for (PersonsPositions pp : ppList) {
            PersonsPositionsUtil.addPersonsPositions(pp);
        }
    }
}
