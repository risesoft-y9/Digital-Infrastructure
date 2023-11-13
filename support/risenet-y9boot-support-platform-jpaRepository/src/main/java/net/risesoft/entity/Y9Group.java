package net.risesoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import lombok.Data;

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
@Comment("用户组表")
@Data
public class Y9Group extends Y9OrgBase {

    private static final long serialVersionUID = -8480745083494990707L;

    public Y9Group() {
        super.setOrgType(OrgTypeEnum.GROUP);
    }

    /** 父节点id */
    @Column(name = "PARENT_ID", length = 38, nullable = false)
    @Comment("父节点id")
    private String parentId;

    /**
     * 岗位组或者用户组
     * 
     * {@link GroupTypeEnum}
     */
    @ColumnDefault("'person'")
    @Column(name = "TYPE", length = 10, nullable = false)
    @Comment("类型：position、person")
    @Convert(converter = EnumConverter.GroupTypeEnumConverter.class)
    private GroupTypeEnum type = GroupTypeEnum.PERSON;

}