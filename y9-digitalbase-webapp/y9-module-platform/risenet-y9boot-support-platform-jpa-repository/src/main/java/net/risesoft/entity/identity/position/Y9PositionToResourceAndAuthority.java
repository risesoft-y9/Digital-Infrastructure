package net.risesoft.entity.identity.position;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.identity.Y9IdentityToResourceAndAuthorityBase;

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
@Comment("岗位与（资源、权限）关系表")
@NoArgsConstructor
@Data
public class Y9PositionToResourceAndAuthority extends Y9IdentityToResourceAndAuthorityBase {

    private static final long serialVersionUID = -8527781135976550912L;

    /** 身份(岗位)唯一标识 */
    @Column(name = "POSITION_ID", length = 38, nullable = false)
    @Comment("身份(岗位)唯一标识")
    private String positionId;

}
