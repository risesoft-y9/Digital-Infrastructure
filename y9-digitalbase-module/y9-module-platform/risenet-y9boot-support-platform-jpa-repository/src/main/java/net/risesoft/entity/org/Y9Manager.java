package net.risesoft.entity.org;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.enums.platform.org.SexEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Manager;
import net.risesoft.persistence.EnumConverter;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

/**
 * 三员 不在组织架构树中展示
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_MANAGER")
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "三员表", appliesTo = "Y9_ORG_MANAGER")
@Data
@NoArgsConstructor
public class Y9Manager extends Y9OrgBase {

    private static final long serialVersionUID = -6531424704457510017L;

    {
        super.setOrgType(OrgTypeEnum.MANAGER);
    }

    /** 父节点id */
    @Column(name = "PARENT_ID", length = 38, nullable = false)
    @Comment("父节点id")
    private String parentId;

    /**
     * 头像
     */
    @Column(name = "AVATOR", length = 100)
    @Comment("头像")
    private String avator;

    /**
     * 电子邮件
     */
    @Column(name = "EMAIL", length = 255)
    @Comment("电子邮箱")
    private String email;

    /**
     * 登录名
     */
    @Column(name = "LOGIN_NAME", length = 255, nullable = false)
    @Comment("登录名")
    private String loginName;

    /**
     * 手机号码
     */
    @Column(name = "MOBILE", length = 255)
    @Comment("手机号码")
    private String mobile;

    /**
     * 登录密码
     */
    @Column(name = "PASSWORD", length = 255)
    @Comment("登录密码")
    private String password;

    /**
     * 排序序列号
     */
    @Column(name = "ORDERED_PATH", length = 500)
    @Comment("排序序列号")
    private String orderedPath;

    /** 性别 */
    @ColumnDefault("1")
    @Column(name = "SEX", nullable = false)
    @Comment("性别")
    @Convert(converter = EnumConverter.SexEnumConverter.class)
    private SexEnum sex = SexEnum.MALE;

    /** 管理员类型 */
    @Column(name = "MANAGER_LEVEL", nullable = false)
    @Comment("管理员类型")
    @ColumnDefault("0")
    @Convert(converter = EnumConverter.ManagerLevelEnumConverter.class)
    private ManagerLevelEnum managerLevel = ManagerLevelEnum.GENERAL_USER;

    /**
     * 区分大三员（true），小三员（false）
     */
    @Type(type = "numeric_boolean")
    @Column(name = "GLOBAL_MANAGER", nullable = false)
    @Comment("是否全局管理员")
    @ColumnDefault("0")
    private Boolean globalManager = false;

    /**
     * 允许访问的客户端IP
     */
    @Comment(value = "允许访问的客户端IP")
    @Column(name = "USER_HOST_IP", length = 150)
    private String userHostIp;

    /**
     * 修改密码时间
     */
    @Comment("上一次密码修改时间")
    @Column(name = "LAST_MODIFY_PASSWORD_TIME")
    private Date lastModifyPasswordTime;

    /**
     * 上一次审查时间
     */
    @Comment(value = "上一次审查时间")
    @Column(name = "LAST_REVIEW_LOG_TIME")
    private Date lastReviewLogTime;

    public Y9Manager(Manager manager, Y9OrgBase parent, Integer nextSubTabIndex, List<Y9OrgBase> ancestorList, String defaultPassword) {
        Y9BeanUtil.copyProperties(manager, this);
        
        if (StringUtils.isBlank(this.id)) {
            this.id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        }
        this.tabIndex = nextSubTabIndex;
        // 系统管理员新建的子域三员默认禁用 需安全管理员启用
        this.disabled = Boolean.TRUE;
        this.password = Y9MessageDigestUtil.bcrypt(defaultPassword);
        this.dn = Y9OrgUtil.buildDn(OrgTypeEnum.MANAGER, this.name, parent.getDn());
        this.guidPath = Y9OrgUtil.buildGuidPath(parent.getGuidPath(), this.id);
        this.orderedPath = Y9OrgUtil.buildOrderedPath(this, ancestorList);;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    public Boolean changeDisabled() {
        boolean targetStatus = !this.disabled;
        this.disabled = targetStatus;
        return targetStatus;
    }

    public void changePassword(String newPassword) {
        this.password = Y9MessageDigestUtil.bcrypt(newPassword);
        this.lastModifyPasswordTime = new Date();
    }

    public boolean isPasswordCorrect(String password) {
        return Y9MessageDigestUtil.bcryptMatch(password, this.password);
    }

    public Boolean isPasswordExpired(int passwordModifiedCycle) {
        Date lastModifyPasswordTime = this.lastModifyPasswordTime;
        if (lastModifyPasswordTime == null) {
            return true;
        }
        long daysBetween = DateUtil.between(lastModifyPasswordTime, new Date(), DateUnit.DAY);
        return daysBetween >= passwordModifiedCycle;
    }

    public boolean isDeptManagerOf(Y9OrgBase managerParent, Y9OrgBase targetOrgBase) {
        if (Boolean.TRUE.equals(this.globalManager)) {
            return true;
        } else {
            if (Y9OrgUtil.isSameOf(managerParent, targetOrgBase)) {
                // 部门三员所在部门
                return true;
            }
            // 部门三员管理部门的后代部门
            return Y9OrgUtil.isDescendantOf(targetOrgBase, managerParent);
        }
    }
}