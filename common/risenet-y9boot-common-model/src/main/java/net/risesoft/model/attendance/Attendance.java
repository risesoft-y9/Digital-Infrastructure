package net.risesoft.model.attendance;

import java.io.Serializable;

import lombok.Data;

/**
 * 考勤管理中年假探亲假实体映射
 * 
 * @author GaoAi
 *
 */
@Data
public class Attendance implements Serializable {

    private static final long serialVersionUID = -5428442928793924267L;

    // 主键
    private String Id;

    // 年份
    private String year;

    // 人员ID
    private String personId;

    // 姓名
    private String username;

    // 应休年假
    private String actualAnnualLeave;

    // 已休年假
    private String alreadyAnnualLeave;

    // 应休探亲假
    private String actualVisitLeave;

    // 已休探亲假
    private String alreadyVisitLeave;

}
