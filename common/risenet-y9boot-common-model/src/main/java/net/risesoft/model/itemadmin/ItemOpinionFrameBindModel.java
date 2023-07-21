package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ItemOpinionFrameBindModel implements Serializable {

    private static final long serialVersionUID = -6583167350240344324L;

    /**
     * 意见框和流程定义节点绑定唯一标示
     */
    private String id;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 意见框唯一标示
     */
    private String opinionFrameMark;

    /**
     * 意见框名称
     */
    private String opinionFrameName;

    /**
     * 事项Id
     */
    private String itemId;

    /**
     * 流程定义Id
     */
    private String processDefinitionId;

    /**
     * 流程定义节点名称
     */
    private String taskDefKey;

    /**
     * 流程定义节点key
     */
    private String taskDefName;

    /**
     * 角色名称字符串
     */
    private String roleNames;

    /**
     * 角色Id集合
     */
    private List<String> roleIds;

    /**
     * 操作的人员的名称
     */
    private String userName;

    /**
     * 最后操作的人员的Id
     */
    private String userId;

    /**
     * 生成时间
     */
    private String createDate;

    /**
     * 最后的修改时间
     */
    private String modifyDate;

}
