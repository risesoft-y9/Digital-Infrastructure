package com.example;

import java.util.List;
import java.util.Map;

import net.risesoft.model.platform.PersonsPositions;
import net.risesoft.model.platform.Position;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;

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
     * @param dataMap
     */
    @SuppressWarnings("unchecked")
    public static void syncPosition(Map<String, Object> dataMap) {
        String syncId = (String)dataMap.get(Y9OrgEventConst.SYNC_ID);
        Integer syncRecursion = (Integer)dataMap.get(Y9OrgEventConst.SYNC_RECURSION);
        Position p = (Position)dataMap.get(syncId);
        boolean exist = checkPositionExist(p);
        if (exist) {
            // updatePosition(p);
        } else {
            // addPosition(p);
        }
        /**
         * 递归，获取岗位中的人员，保存岗位人员绑定关系
         */
        if (syncRecursion == 1) {
            List<PersonsPositions> ppList = (List<PersonsPositions>)dataMap.get(syncId + "PersonsPositions");
            for (PersonsPositions pp : ppList) {
                PersonsPositionsUtil.addPersonsPositions(pp);
            }
        }
    }
}
