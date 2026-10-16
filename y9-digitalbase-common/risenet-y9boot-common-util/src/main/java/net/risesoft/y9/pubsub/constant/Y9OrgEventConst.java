package net.risesoft.y9.pubsub.constant;

/**
 * 组织事件类型常量类
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public class Y9OrgEventConst {

    // 事件接收系统默认为所有系统
    public static final String EVENT_TARGET_ALL = "ALL";

    /** 组织类型 */
    public static final String ORG_TYPE = "ORG_TYPE";
    /** 同步标识 */
    public static final String SYNC_ID = "SYNC_ID";

    // 是否递归
    public static final String SYNC_RECURSION = "SYNC_RECURSION";
    /** 需要递归 */
    public static final Integer SYNC_NEED_RECURSION = 1;
    /** 不需要递归 */
    public static final Integer SYNC_DONT_RECURSION = 0;

    /** 人员与用户组映射关系 */
    public static final String ORG_MAPPING_PERSONS_GROUPS = "PersonsGroups";
    /** 人员与岗位映射关系 */
    public static final String ORG_MAPPING_PERSONS_POSITIONS = "PersonsPositions";

}
