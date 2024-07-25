package net.risesoft.y9.sqlddl.pojo;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 数据库列表信息
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DbColumn implements Serializable {
    private static final long serialVersionUID = -7176298428774384422L;

    /**
     * 列名
     */
    private String columnName;
    /**
     * 旧列名
     */
    private String columnNameOld;
    /**
     * 字段类型
     */
    private int dataType;
    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 字段长度
     */
    private Integer dataLength;
    /**
     * 字段精度
     */
    private Integer dataPrecision;
    /**
     * 小数位数
     */
    private Integer dataScale;
    /**
     * 所属表名
     */
    private String tableName;
    /**
     * 主键
     */
    private Boolean primaryKey;
    /**
     * 能否为空
     */
    private Boolean nullable;
    /**
     * 字段备注，用来中文化
     */
    private String comment;
    /**
     * 是否主键
     */
    private Integer isPrimaryKey;

    /***
     * 是否允空
     */
    private Integer isNull;

}
