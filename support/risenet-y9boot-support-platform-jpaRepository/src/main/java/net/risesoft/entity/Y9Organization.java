package net.risesoft.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import lombok.Data;

import net.risesoft.enums.OrgTypeEnum;

/**
 * 组织机构实体
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_ORGANIZATION")
@org.hibernate.annotations.Table(comment = "组织机构实体表", appliesTo = "Y9_ORG_ORGANIZATION")
@Data
public class Y9Organization extends Y9OrgBase {

    private static final long serialVersionUID = -5379834937852013780L;

    public Y9Organization() {
        super.setOrgType(OrgTypeEnum.ORGANIZATION.getEnName());
    }

    /** 英文名称 */
    @Column(name = "EN_NAME", length = 255)
    @Comment("英文名称")
    private String enName;

    /** 组织机构代码 */
    @Column(name = "ORGANIZATION_CODE", length = 255)
    @Comment("组织机构代码")
    private String organizationCode;

    /** 组织机构类型 */
    @Column(name = "ORGANIZATION_TYPE", length = 255)
    @Comment("组织机构类型")
    private String organizationType;

    /** 类型:0=实体组织，1=虚拟组织 */
    @ColumnDefault("0")
    @Column(name = "VIRTUALIZED", nullable = false)
    @Type(type = "numeric_boolean")
    @Comment("类型:0=实体组织，1=虚拟组织")
    private Boolean virtual = false;

}