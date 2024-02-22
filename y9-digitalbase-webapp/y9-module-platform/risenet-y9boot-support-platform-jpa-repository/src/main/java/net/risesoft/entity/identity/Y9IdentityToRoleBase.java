package net.risesoft.entity.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 人或岗位与角色关系表基类
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/2/10
 */
@MappedSuperclass
@NoArgsConstructor
@Data
public class Y9IdentityToRoleBase extends BaseEntity {
    private static final long serialVersionUID = -1406915289962175747L;

    /** id */
    @Id
    @Column(name = "ID")
    @Comment("主键id")
    protected String id;

    /** 租户id */
    @Column(name = "TENANT_ID", length = 38)
    @Comment("租户id")
    protected String tenantId;

    /** 部门id */
    @Column(name = "DEPARTMENT_ID", length = 38, nullable = false)
    @Comment("部门id")
    protected String departmentId;

    /** 角色id */
    @Column(name = "ROLE_ID", length = 38, nullable = false)
    @Comment("角色id")
    protected String roleId;

    /** 角色自定义id */
    @Column(name = "ROLE_CUSTOM_ID", length = 38)
    @Comment("角色自定义id")
    protected String roleCustomId;

    /** 应用id */
    @Column(name = "APP_ID", length = 38)
    @Comment("应用id")
    protected String appId;

    /** 系统id */
    @Column(name = "SYSTEM_ID", length = 38)
    @Comment("系统id")
    protected String systemId;

    /** 描述 */
    @Column(name = "DESCRIPTION", length = 255)
    @Comment("描述")
    protected String description;

    /** 角色名称 */
    @Column(name = "ROLE_NAME", length = 255)
    @Comment("角色名称")
    protected String roleName;

    /** 系统英文名称 */
    @Column(name = "SYSTEM_NAME", length = 255)
    @Comment("系统英文名称")
    protected String systemName;

    /** 系统中文名称 */
    @Column(name = "SYSTEM_CN_NAME", length = 255)
    @Comment("系统中文名称")
    protected String systemCnName;

    /** 应用名称 */
    @Column(name = "APP_NAME", length = 255)
    @Comment("应用名称")
    protected String appName;

}
