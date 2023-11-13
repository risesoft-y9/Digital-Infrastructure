package net.risesoft.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

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
@org.hibernate.annotations.Table(comment = "删除的组织表", appliesTo = "Y9_ORGBASE_DELETED")
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