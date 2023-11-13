package net.risesoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 删除的组织表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORGBASE_DELETED")
@Comment("删除的组织表")
@NoArgsConstructor
@Data
public class Y9OrgBaseDeleted extends BaseEntity {

    private static final long serialVersionUID = -5165264734456244849L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    protected String id;

    /** 组织id */
    @Column(name = "ORG_ID", length = 38)
    @Comment("组织id")
    protected String orgId;

    /** 父节点的Id */
    @Column(name = "PARENT_ID", length = 38)
    @Comment("父节点的Id")
    protected String parentId;

    /**
     * 组织类型
     *
     * {@link OrgTypeEnum}
     */
    @Column(name = "ORG_TYPE", length = 255, nullable = false)
    @Comment("组织类型")
    @Convert(converter = EnumConverter.OrgTypeEnumConverter.class)
    protected OrgTypeEnum orgType;

    /** 名称组成的父子关系，之间以逗号分割 */
    @Column(name = "DN", length = 2000)
    @Comment("名称组成的父子关系，之间以逗号分割")
    protected String dn;

    /** 操作者 */
    @Column(name = "OPERATOR", length = 30)
    @Comment("操作者")
    protected String operator;

    /** 内容 */
    @Lob
    @Column(name = "JSON_CONTENT")
    @Comment("内容")
    protected String jsonContent;

}