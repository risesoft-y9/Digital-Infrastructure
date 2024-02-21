package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 协作状态详情
 *
 * @author Think
 *
 */
@Data
public class ProcessInstanceDetailsModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6067789541304948656L;

    private String id;

    // @Comment("流程实例id")
    private String processInstanceId;

    // @Comment("流程编号")
    private String processSerialNumber;

    // @Comment("任务id")
    private String taskId;

    // @Comment("任务名称")
    private String taskName;

    // @Comment("意见内容")
    private String opinionContent;

    // @Comment("系统英文名称")
    private String systemName;

    // @Comment("系统中文名称")
    private String systemCnName;

    // @Comment("事项id")
    private String itemId;

    // @Comment("应用名称")
    private String appName;

    // @Comment("应用中文名称")
    private String appCnName;

    // @Comment("发送人id")
    private String senderId;

    // @Comment("发送人名称")
    private String senderName;

    // @Comment("办理人id")
    private String assigneeId;

    // @Comment("办理人名称")
    private String assigneeName;

    // @Comment("开始时间")
    private Date startTime;

    // @Comment("结束时间")
    private Date endTime;

    // @Comment("编号")
    private String serialNumber;

    // @Comment("文件标题")
    private String title;

    // @Comment("发起人")
    private String userName;

    // @Comment("办件状态")
    private String itembox;// todo待办，doing在办，done办结

    // @Comment("详情链接")
    private String url;

}
