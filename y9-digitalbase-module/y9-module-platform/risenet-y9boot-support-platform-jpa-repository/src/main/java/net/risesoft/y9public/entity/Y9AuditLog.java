package net.risesoft.y9public.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.base.BaseEntity;

/**
 * 审计日志表
 *
 * @author shidaobang
 * @date 2025/08/06
 */
@Entity
@Table(name = "Y9_AUDIT_LOG", indexes = {@Index(name = "IDX_OBJECT_ID", columnList = "OBJECT_ID")})
@DynamicUpdate
@Comment("审计日志")
@NoArgsConstructor
@Data
@SuperBuilder
@Slf4j
public class Y9AuditLog extends BaseEntity {

    private static final long serialVersionUID = -8840481033270780042L;

    /**
     * 主键
     */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /**
     * 租户 id
     */
    @Column(name = "TENANT_ID", length = 38)
    @Comment("租户 id")
    private String tenantId;

    /**
     * 用户 id
     */
    @Column(name = "USER_ID", length = 38)
    @Comment(value = "用户 id")
    private String userId;

    /**
     * 用户名
     */
    @Column(name = "USER_NAME", length = 100)
    @Comment(value = "用户名")
    private String userName;

    /**
     * 用户 IP
     */
    @Column(name = "USER_IP", length = 50)
    @Comment(value = "用户IP")
    private String userIp;

    /**
     * 用户浏览器或客户端信息
     */
    @Column(name = "USER_AGENT", length = 200)
    @Comment(value = "用户浏览器或客户端信息")
    private String userAgent;

    /**
     * 操作类型
     */
    @Column(name = "ACTION", length = 50)
    @Comment(value = "操作类型")
    private String action;

    /**
     * 操作简要描述
     */
    @Column(name = "DESCRIPTION", length = 200)
    @Comment(value = "操作简要描述")
    private String description;

    /**
     * 操作对象 id
     */
    @Column(name = "OBJECT_ID", length = 38)
    @Comment("操作对象 id")
    private String objectId;

    /**
     * 旧对象 JSON
     */
    @Lob
    @Column(name = "OLD_OBJECT_JSON")
    @Comment("旧对象 JSON")
    private String oldObjectJson;

    /**
     * 当前对象 JSON
     */
    @Lob
    @Column(name = "CURRENT_OBJECT_JSON")
    @Comment("当前对象 JSON")
    private String currentObjectJson;
}
