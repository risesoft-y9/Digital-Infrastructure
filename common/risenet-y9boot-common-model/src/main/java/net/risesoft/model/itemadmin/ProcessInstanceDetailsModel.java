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

    // @org.hibernate.annotations.Comment("流程实例id")
    private String processInstanceId;

    // @org.hibernate.annotations.Comment("流程编号")
    private String processSerialNumber;

    // @org.hibernate.annotations.Comment("任务id")
    private String taskId;

    // @org.hibernate.annotations.Comment("任务名称")
    private String taskName;

    // @org.hibernate.annotations.Comment("意见内容")
    private String opinionContent;

    // @org.hibernate.annotations.Comment("系统英文名称")
    private String systemName;

    // @org.hibernate.annotations.Comment("系统中文名称")
    private String systemCnName;

    // @org.hibernate.annotations.Comment("事项id")
    private String itemId;

    // @org.hibernate.annotations.Comment("应用名称")
    private String appName;

    // @org.hibernate.annotations.Comment("应用中文名称")
    private String appCnName;

    // @org.hibernate.annotations.Comment("发送人id")
    private String senderId;

    // @org.hibernate.annotations.Comment("发送人名称")
    private String senderName;

    // @org.hibernate.annotations.Comment("办理人id")
    private String assigneeId;

    // @org.hibernate.annotations.Comment("办理人名称")
    private String assigneeName;

    // @org.hibernate.annotations.Comment("开始时间")
    private Date startTime;

    // @org.hibernate.annotations.Comment("结束时间")
    private Date endTime;

    // @org.hibernate.annotations.Comment("编号")
    private String serialNumber;

    // @org.hibernate.annotations.Comment("文件标题")
    private String title;

    // @org.hibernate.annotations.Comment("发起人")
    private String userName;

    // @org.hibernate.annotations.Comment("办件状态")
    private String itembox;// todo待办，doing在办，done办结

    // @org.hibernate.annotations.Comment("详情链接")
    private String url;

}
