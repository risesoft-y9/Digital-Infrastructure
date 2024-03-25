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

    public static final String ORG_TYPE = "ORG_TYPE";
    public static final String SYNC_ID = "SYNC_ID";

    // 是否递归
    public static final String SYNC_RECURSION = "SYNC_RECURSION";
    public static final Integer SYNC_NEED_RECURSION = 1;
    public static final Integer SYNC_DONT_RECURSION = 0;

    public static final String ORG_MAPPING_PERSONS_GROUPS = "PersonsGroups";
    public static final String ORG_MAPPING_PERSONS_POSITIONS = "PersonsPositions";

}
