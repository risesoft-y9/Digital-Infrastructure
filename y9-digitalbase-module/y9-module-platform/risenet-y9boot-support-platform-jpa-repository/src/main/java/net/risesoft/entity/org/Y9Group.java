package net.risesoft.entity.org;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import net.risesoft.enums.platform.org.GroupTypeEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
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
@Comment("用户组表")
@Data
@SuperBuilder
@NoArgsConstructor
public class Y9Group extends Y9OrgBase {

    private static final long serialVersionUID = -8480745083494990707L;

    {
        super.setOrgType(OrgTypeEnum.GROUP);
    }

    /** 父节点id */
    @Column(name = "PARENT_ID", length = 38, nullable = false)
    @Comment("父节点id")
    private String parentId;

    /** 岗位组或者用户组 */
    @ColumnDefault("'person'")
    @Column(name = "TYPE", length = 10, nullable = false)
    @Comment("类型：position、person")
    @Convert(converter = EnumConverter.GroupTypeEnumConverter.class)
    @Builder.Default
    private GroupTypeEnum type = GroupTypeEnum.PERSON;

    @Override
    public String getParentId() {
        return this.parentId;
    }
}