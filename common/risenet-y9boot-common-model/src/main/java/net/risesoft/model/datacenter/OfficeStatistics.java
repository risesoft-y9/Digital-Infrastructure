package net.risesoft.model.datacenter;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 即时数据-办件统计
 *
 * @author Think
 *
 */
@Data
public class OfficeStatistics implements Serializable {

    private static final long serialVersionUID = -3165363350463260123L;

    /**
     * 主键
     */
    private String id;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 人员id
     */
    private String userId;

    /**
     * 人员姓名
     */
    private String userName;

    /**
     * 所属部门id
     */
    private String deptGuid;

    /**
     * 所属部门名称
     */
    private String deptName;

    /**
     * 系统英文名称
     */
    private String systemName;

    /**
     * 系统中文名称
     */
    private String systemCnName;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用中文名称
     */
    private String appCnName;

    /**
     * 今日办结统计
     */
    private Integer todayHandleCount;

    /**
     * 今日发文统计
     */
    private Integer todaySendCount;

    /**
     * 今日收文统计
     */
    private Integer todayReceiveCount;

    /**
     * 未办件统计
     */
    private Integer notHandleCount;

    /**
     * 已办理统计
     */
    private Integer allHandleCount;

    /**
     * 更新时间
     */
    private Date updateTime;

}
