package net.risesoft.y9public.entity.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

@Entity
@Table(name = "Y9_ORG_SYNC_ROLE")
@org.hibernate.annotations.Table(comment = "组织架构同步权限表", appliesTo = "Y9_ORG_SYNC_ROLE")
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
