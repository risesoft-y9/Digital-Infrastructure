package net.risesoft.model.datacenter;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 办件历程信息
 * 
 * @author Think
 *
 */
@Data
public class HistoryInfo implements Serializable {

    private static final long serialVersionUID = 3234095140068450696L;

    /**
     * 办件人
     */
    private String assignee;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 办理环节
     */
    private String actionName;

    /**
     * 意见内容
     */
    private String opinionContent;

}
