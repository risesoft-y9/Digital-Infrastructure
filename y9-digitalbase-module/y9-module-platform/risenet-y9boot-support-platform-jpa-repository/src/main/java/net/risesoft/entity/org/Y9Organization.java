package net.risesoft.entity.org;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import net.risesoft.enums.platform.org.OrgTypeEnum;

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
@DynamicUpdate
@Comment("组织机构实体表")
@Data
@SuperBuilder
public class Y9Organization extends Y9OrgBase {

    private static final long serialVersionUID = -5379834937852013780L;

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
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Comment("类型:0=实体组织，1=虚拟组织")
    private Boolean virtual = false;

    public Y9Organization() {
        super.setOrgType(OrgTypeEnum.ORGANIZATION);
    }

}