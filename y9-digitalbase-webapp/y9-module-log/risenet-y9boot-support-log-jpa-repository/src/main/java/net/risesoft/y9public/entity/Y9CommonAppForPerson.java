package net.risesoft.y9public.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 个人常用应用信息表
 *
 * @author mengjuhua
 * @date 2024/04/23
 */
@Entity
@Table(name = "Y9_LOG_COMMON_APP_FOR_PERSON")
@Comment("个人常用应用信息表")
@NoArgsConstructor
@Data
public class Y9CommonAppForPerson implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6201670055765212286L;

    /** 主键，唯一标识 */
    @Id
    @Column(name = "ID")
    @Comment("主键")
    private String id;

    /** 用户ID */
    @Column(name = "PERSON_ID", length = 100, nullable = false)
    @Comment("用户ID ")
    private String personId;

    /** 租户ID */
    @Column(name = "TENANT_ID", length = 38, nullable = false)
    @Comment("租户ID ")
    private String tenantId;

    /** 应用IDS */
    @Lob
    @Column(name = "APP_IDS", nullable = true)
    @Comment("应用IDS ")
    private String appIds;
}
