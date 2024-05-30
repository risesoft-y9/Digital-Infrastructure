package net.risesoft.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import net.risesoft.enums.platform.GroupTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.persistence.EnumConverter;

/**
 * 用户组
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_GROUP")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "用户组表", appliesTo = "Y9_ORG_GROUP")
@Data
@SuperBuilder
public class Y9Group extends Y9OrgBase {

    private static final long serialVersionUID = -8480745083494990707L;
    /** 父节点id */
    @Column(name = "PARENT_ID", length = 38, nullable = false)
    @Comment("父节点id")
    private String parentId;

    /** 岗位组或者用户组 */
    @ColumnDefault("'person'")
    @Column(name = "TYPE", length = 10, nullable = false)
    @Comment("类型：position、person")
    @Convert(converter = EnumConverter.GroupTypeEnumConverter.class)
    private GroupTypeEnum type = GroupTypeEnum.PERSON;

    public Y9Group() {
        super.setOrgType(OrgTypeEnum.GROUP);
    }

}