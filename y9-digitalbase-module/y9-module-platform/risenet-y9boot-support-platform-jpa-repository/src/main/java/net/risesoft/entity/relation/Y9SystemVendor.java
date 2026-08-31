package net.risesoft.entity.relation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;

/**
 * 系统开发商和系统关联关系
 *
 * @author shidaobang
 * @date 2026/8/31
 */
@Entity
@Table(name = "Y9_COMMON_SYSTEM_VENDOR", indexes = {@Index(columnList = "MANAGER_ID,SYSTEM_ID", unique = true)})
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "系统开发商和系统关联表", appliesTo = "Y9_COMMON_SYSTEM_VENDOR")
@Data
@NoArgsConstructor
public class Y9SystemVendor extends BaseEntity {

    private static final long serialVersionUID = -2823457667855911717L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /** 管理员 id */
    @Column(name = "MANAGER_ID", length = 38, nullable = false)
    @Comment("管理员 id")
    private String managerId;

    /** 系统 id */
    @Column(name = "SYSTEM_ID", length = 38, nullable = false)
    @Comment("系统 id")
    private String systemId;

    public Y9SystemVendor(String managerId, String systemId) {
        this.id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        this.managerId = managerId;
        this.systemId = systemId;
    }
}
