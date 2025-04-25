package net.risesoft.entity.permission;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.consts.DefaultConsts;

/**
 * 数据行权限表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_PERMISSION_DATAROW")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "数据行权限表", appliesTo = "Y9_ORG_PERMISSION_DATAROW")
@NoArgsConstructor
@Data
public class Y9DataRowPermission extends BaseEntity {

    private static final long serialVersionUID = 8456154957926701479L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /** 应用id */
    @Column(name = "APP_ID", length = 38)
    @Comment("应用id")
    private String appId;

    /** 名称 */
    @Column(name = "NAME", length = 255)
    @Comment("名称")
    private String name;

    /** 描述 */
    @Column(name = "DESCRIPTION", length = 255)
    @Comment("描述")
    private String description;

    /** 数据范围 */
    @Column(name = "LIMIT_CLAUSE", length = 2000)
    @Comment("数据范围")
    private String limitClause;

    /** 排序号 */
    @Column(name = "TAB_INDEX", nullable = false)
    @Comment("排序号")
    private Integer tabIndex = DefaultConsts.TAB_INDEX;

    /*
     * @Column(name = "TABLE_NAME", length = 40)
     * 
     * @FieldCommit(value = "表名称") private String tableName;
     * 
     * @Column(name = "FIELD_NAME", length = 40)
     * 
     * @FieldCommit(value = "字段名称") private String fieldName;
     * 
     * @Column(name = "SQL_OPERATOR", length = 20)
     * 
     * @FieldCommit(value = "SQL运算符") private String operator;
     * 
     * @Column(name = "FIELD_VALUE", length = 255)
     * 
     * @FieldCommit(value = "字段值") private String fieldValue;
     */
}
