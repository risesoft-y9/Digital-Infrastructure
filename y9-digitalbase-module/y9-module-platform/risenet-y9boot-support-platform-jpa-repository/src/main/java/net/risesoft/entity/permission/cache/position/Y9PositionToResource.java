package net.risesoft.entity.permission.cache.position;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.permission.cache.Y9IdentityToResourceBase;

/**
 * 岗位与（资源、权限）关系表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_POSITIONS_RESOURCES",
    indexes = {@Index(columnList = "POSITION_ID,RESOURCE_ID,AUTHORIZATION_ID,AUTHORITY", unique = true)})
@DynamicUpdate
@Comment("岗位与（资源、权限）关系表")
@NoArgsConstructor
@Data
public class Y9PositionToResource extends Y9IdentityToResourceBase {

    private static final long serialVersionUID = -8527781135976550912L;

    /** 身份(岗位)唯一标识 */
    @Column(name = "POSITION_ID", length = 38, nullable = false)
    @Comment("身份(岗位)唯一标识")
    private String positionId;

}
