package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemModel implements Serializable {

    private static final long serialVersionUID = -2211904374246635797L;

    private String id;// 主键

    private String name;// 事项名称

    private String type; // 事项类型

    private String accountability; // 事项责任制

    private String nature; // 事项管理员

    private String sysLevel; // 系统级别

    private Integer legalLimit; // 法定期限

    private Integer expired; // 承诺时限

    private String workflowGuid; // 工作流GUID

    private String isOnline; // 是否网上申办，1“是”、0“否”

    private String isDocking; // 是否对接，1“是”、0“否”

    private String dockingSystem;// 对接外部系统标识

    private String dockingItemId;// 对接事项id

    private String systemName;

    private String todoTaskUrlPrefix;

    private String iconData;// 图标内容

    private Boolean customItem;// 是否可定制事项

}