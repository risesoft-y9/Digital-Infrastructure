package net.risesoft.entity.org;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.Y9BeanUtil;

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
@org.hibernate.annotations.Table(comment = "组织机构实体表", appliesTo = "Y9_ORG_ORGANIZATION")
@Data
@NoArgsConstructor
public class Y9Organization extends Y9OrgBase {

    private static final long serialVersionUID = -5379834937852013780L;

    {
        super.setOrgType(OrgTypeEnum.ORGANIZATION);
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

    public Y9Organization(Organization organization, Integer nextTabIndex) {
        Y9BeanUtil.copyProperties(organization, this);

        if (StringUtils.isBlank(this.id)) {
            this.id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        }
        if (DefaultConsts.TAB_INDEX.equals(this.tabIndex)) {
            this.tabIndex = nextTabIndex;
        }
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.ORGANIZATION, this.name, null);
        this.guidPath = Y9OrgUtil.buildGuidPath(null, this.id);
    }

    @Override
    public String getParentId() {
        return null;
    }

    public void changeProperties(String properties) {
        this.properties = properties;
    }

    public Boolean changeDisabled(boolean isAllDescendantsDisabled) {
        boolean targetStatus = !this.disabled;
        if (targetStatus && !isAllDescendantsDisabled) {
            throw Y9ExceptionUtil.businessException(OrgUnitErrorCodeEnum.NOT_ALL_DESCENDENTS_DISABLED);
        }
        this.disabled = targetStatus;
        return targetStatus;
    }

    public void update(Organization organization) {
        Y9BeanUtil.copyProperties(organization, this);

        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.ORGANIZATION, this.name, null);
        this.guidPath = Y9OrgUtil.buildGuidPath(null, this.id);
    }

    public void changeTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }
}
