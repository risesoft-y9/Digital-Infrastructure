package net.risesoft.entity.permission.cache;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

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

    /** 角色id */
    @Column(name = "ROLE_ID", length = 38, nullable = false)
    @Comment("角色id")
    protected String roleId;

    /** 应用id */
    @Column(name = "APP_ID", length = 38)
    @Comment("应用id")
    protected String appId;

    /** 系统id */
    @Column(name = "SYSTEM_ID", length = 38)
    @Comment("系统id")
    protected String systemId;

}
