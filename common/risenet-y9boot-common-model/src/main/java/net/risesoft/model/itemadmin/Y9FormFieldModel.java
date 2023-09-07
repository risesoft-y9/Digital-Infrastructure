package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class Y9FormFieldModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -342974331407241039L;

    // 主键
    private String id;

    // 表单Id
    private String formId;

    // 对应的表id
    private String tableId;

    // 对应的表名称
    private String tableName;

    // 字段名称
    private String fieldName;

    // 字段中文名称
    private String fieldCnName;

    // 字段类型
    private String fieldType;

    // 开启查询条件
    private String querySign = "0";

    // 查询类型
    private String queryType;

    // 多选框，单选框选项值
    private String optionValue;

}
