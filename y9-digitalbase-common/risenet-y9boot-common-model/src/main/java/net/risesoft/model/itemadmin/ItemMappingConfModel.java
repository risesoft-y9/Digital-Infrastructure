package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemMappingConfModel implements Serializable {

    private static final long serialVersionUID = -2211904374246635797L;

    private String id;

    // @FieldCommit(value = "事项Id")
    private String itemId;

    // @FieldCommit(value = "对接系统类型")//1为内部系统，2为外部系统
    private String sysType = "2";

    // @FieldCommit(value = "表名称")
    private String tableName;

    // @FieldCommit(value = "列名称")
    private String columnName;

    // @FieldCommit(value = "映射系统标识")//内部系统为事项id，外部系统为自定义标识
    private String mappingId;

    // @FieldCommit(value = "映射表名称")//sysType为1时使用
    private String mappingTableName;

    // @FieldCommit(value = "映射字段名")
    private String mappingName;

    // @FieldCommit(value = "生成时间")
    private String createTime;

}