package net.risesoft.y9public.entity.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

@Entity
@Table(name = "Y9_ORG_SYNC_ROLE")
@DynamicUpdate
@Comment(  "组织架构同步权限表" )
@NoArgsConstructor
@Data
public class Y9OrgSyncRole extends BaseEntity {

    private static final long serialVersionUID = 1370381862846471287L;

    @Id
    @Column(name = "APP_NAME", length = 100, nullable = false)
    @Comment("第三方应用名称")
    private String appName;

    @Column(name = "TENANT_ID", length = 38, nullable = false)
    @Comment("租户id")
    private String tenantId;

    @Column(name = "ORG_IDS", length = 1000, nullable = false)
    @Comment("同步的节点id")
    private String orgIds;

}
