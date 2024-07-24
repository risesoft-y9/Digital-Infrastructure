package net.risesoft.y9.sqlddl;

import java.io.Serializable;
import java.util.Objects;

/**
 * 数据库列表信息
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
public class DbColumn implements Serializable {
    private static final long serialVersionUID = -7176298428774384422L;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DbColumn other = (DbColumn)obj;
        return Objects.equals(columnName, other.columnName) && Objects.equals(columnNameOld, other.columnNameOld)
            && Objects.equals(comment, other.comment) && Objects.equals(dataLength, other.dataLength)
            && Objects.equals(dataPrecision, other.dataPrecision) && Objects.equals(dataScale, other.dataScale)
            && dataType == other.dataType && Objects.equals(isNull, other.isNull)
            && Objects.equals(isPrimaryKey, other.isPrimaryKey) && Objects.equals(nullable, other.nullable)
            && Objects.equals(primaryKey, other.primaryKey) && Objects.equals(tableName, other.tableName)
            && Objects.equals(typeName, other.typeName);
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnNameOld() {
        return columnNameOld;
    }

    public String getComment() {
        return comment;
    }

    public Integer getDataLength() {
        return dataLength;
    }

    public Integer getDataPrecision() {
        return dataPrecision;
    }

    public Integer getDataScale() {
        return dataScale;
    }

    public int getDataType() {
        return dataType;
    }

    public Integer getIsNull() {
        return isNull;
    }

    public Integer getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, columnNameOld, comment, dataLength, dataPrecision, dataScale, dataType, isNull,
            isPrimaryKey, nullable, primaryKey, tableName, typeName);
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setColumnNameOld(String columnNameOld) {
        this.columnNameOld = columnNameOld;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDataLength(Integer dataLength) {
        this.dataLength = dataLength;
    }

    public void setDataPrecision(Integer dataPrecision) {
        this.dataPrecision = dataPrecision;
    }

    public void setDataScale(Integer dataScale) {
        this.dataScale = dataScale;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setIsNull(Integer isNull) {
        this.isNull = isNull;
    }

    public void setIsPrimaryKey(Integer isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "DbColumn [columnName=" + columnName + ", columnNameOld=" + columnNameOld + ", dataType=" + dataType
            + ", typeName=" + typeName + ", dataLength=" + dataLength + ", dataPrecision=" + dataPrecision
            + ", dataScale=" + dataScale + ", tableName=" + tableName + ", primaryKey=" + primaryKey + ", nullable="
            + nullable + ", comment=" + comment + ", isPrimaryKey=" + isPrimaryKey + ", isNull=" + isNull + "]";
    }

}
