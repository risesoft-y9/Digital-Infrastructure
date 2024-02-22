package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProcessParamModel implements Serializable {

    private static final long serialVersionUID = 3792751066006296420L;
    /**
     * 主键
     */
    private String id;

    /**
     * 流程实例Id
     */
    private String processInstanceId;
    /**
     * 流程序列号
     */
    private String processSerialNumber;
    /**
     * 事项id
     */
    private String itemId;
    /**
     * 事项id
     */
    private String itemName;
    /**
     * 系统英文名称
     */
    private String systemName;
    /**
     * 系统中文名称
     */
    private String systemCnName;
    /**
     * 标题
     */
    private String title;
    /**
     * 自定义编号
     */
    private String customNumber;
    /**
     * 
     */
    private String customLevel;
    /**
     * 委办局Ids
     */
    private String bureauIds;
    /**
     * 部门ids
     */
    private String deptIds;
    /**
     * 流程办结人员姓名
     */
    private String completer;

    /**
     * 统一待办url前缀
     */
    private String todoTaskUrlPrefix;

    /**
     * 搜索词
     */
    private String searchTerm;

    // @FieldCommit(value="是否发送短信")
    private String isSendSms;

    // @FieldCommit(value="是否署名")
    private String isShuMing;

    // @FieldCommit(value="发送短信内容")
    private String smsContent;

    // @FieldCommit(value="接收短信人员id")
    private String smsPersonId;

    // @FieldCommit(value="主办人id")
    private String sponsorGuid;

    // @FieldCommit(value="流程的启动人员id")
    private String startor;

    // @FieldCommit(value="流程的启动人员姓名")
    private String startorName;

    /**
     * 这个件是否发送过,true为发送过
     */
    private String sended;

    /**
     * 创建时间
     */
    private String createTime;

    private Boolean customItem; // 是否定制流程
}
