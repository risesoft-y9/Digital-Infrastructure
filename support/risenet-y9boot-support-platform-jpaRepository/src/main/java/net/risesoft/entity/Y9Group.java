package net.risesoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import lombok.Data;

import net.risesoft.enums.GroupTypeEnum;
import net.risesoft.enums.OrgTypeEnum;

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
        super.setOrgType(OrgTypeEnum.GROUP.getEnName());
    }

    /**
     * 岗位组或者用户组
     * 
     * {@link GroupTypeEnum}
     */
    @ColumnDefault("'person'")
    @Column(name = "TYPE", length = 10, nullable = false)
    @Comment("类型：position、person")
    private String type = GroupTypeEnum.PERSON.getName();

}