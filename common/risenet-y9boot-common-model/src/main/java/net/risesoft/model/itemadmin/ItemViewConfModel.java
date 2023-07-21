package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemViewConfModel implements Serializable {

    private static final long serialVersionUID = -7907684948565390289L;

    private String id;

    private String itemId;

    private String viewType;

    private String tableName;

    private String columnName;

    private String disPlayName;

    private String disPlayWidth;

    private String disPlayAlign;

    private Integer tabIndex;

    private String userId;

    private String userName;

    private String createTime;

    private String updateTime;

    // 是否开启搜索条件,绑定数据库表和字段时，可开启搜索条件
    private Integer openSearch;

    // 输入框类型,search-带图标前缀的搜索框,input,select,date
    private String inputBoxType;

    // 搜索框宽度
    private String spanWidth;

    // 搜索名称,不填写则使用disPlayName显示名称
    private String labelName;

    // 绑定数据字典,输入框类型select时使用
    private String optionClass;
}
