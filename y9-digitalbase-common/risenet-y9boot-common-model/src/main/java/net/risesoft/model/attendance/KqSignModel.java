package net.risesoft.model.attendance;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class KqSignModel implements Serializable {

    private static final long serialVersionUID = 97162770780348591L;

    private String Id;

    private String username;

    private String personId;

    private String creatTime;

    private Date signStar;

    private String signed;

    private Integer signStatus;

    private Date signEnd;

    private String outed;

    private Integer outStatus;

    private String remarks;

    /**
     * {@link #ItemLeaveTypeEnum}
     */
    private Integer leaveType;

    private String leaveYear;

    // 科室Id
    private String deptId;

    // 委办局ID
    private String breauId;
}
