package net.risesoft.entity.org;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.platform.org.GroupTypeEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Group;
import net.risesoft.persistence.EnumConverter;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 用户组
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_GROUP",
    uniqueConstraints = {@UniqueConstraint(name = Y9Group.UK_CUSTOM_ID, columnNames = {"CUSTOM_ID"})})
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "用户组表", appliesTo = "Y9_ORG_GROUP")
@Data
@NoArgsConstructor
public class Y9Group extends Y9OrgBase {

    private static final long serialVersionUID = -8480745083494990707L;

    public static final String UK_CUSTOM_ID = "UK_GROUP_CUSTOM_ID";

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
    private GroupTypeEnum type = GroupTypeEnum.PERSON;

    public Y9Group(Group group, Y9OrgBase parent, Integer nextSubTabIndex) {
        Y9BeanUtil.copyProperties(group, this);

        if (StringUtils.isBlank(this.id)) {
            this.id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        }
        if (StringUtils.isBlank(this.customId)) {
            // customId 字段有唯一约束，不同数据库对唯一约束的允许不一致，此处保证 customId 列始终有值，所有数据库通用
            this.customId = this.id;
        }
        if (DefaultConsts.TAB_INDEX.equals(this.tabIndex)) {
            this.tabIndex = nextSubTabIndex;
        }
        this.parentId = parent.getId();
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.GROUP, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public String getCustomId() {
        if (StringUtils.equals(this.customId, this.id)) {
            // 对上层隐藏“非必填”的 customId 字段唯一索引的实现细节
            return null;
        }
        return this.customId;
    }

    public Boolean changeDisabled() {
        boolean targetStatus = !this.disabled;
        this.disabled = targetStatus;
        return targetStatus;
    }

    public void changeParent(Y9OrgBase parent, Integer nextSubTabIndex) {
        this.parentId = parent.getId();
        this.tabIndex = nextSubTabIndex;
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.GROUP, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
    }

    public void changeProperties(String properties) {
        this.properties = properties;
    }

    public void changeTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public void update(Group group, Y9OrgBase parent) {
        Y9BeanUtil.copyProperties(group, this);

        if (StringUtils.isBlank(this.customId)) {
            // customId 字段有唯一约束，不同数据库对唯一约束的允许不一致，此处保证 customId 列始终有值，所有数据库通用
            this.customId = this.id;
        }
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.GROUP, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
    }
}