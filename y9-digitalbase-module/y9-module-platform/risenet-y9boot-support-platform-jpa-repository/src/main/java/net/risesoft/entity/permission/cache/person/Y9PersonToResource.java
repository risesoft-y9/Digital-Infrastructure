package net.risesoft.entity.permission.cache.person;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.permission.cache.Y9IdentityToResourceBase;

/**
 * 人员与（资源、权限）关系表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_PERSONS_RESOURCES",
    indexes = {@Index(columnList = "PERSON_ID,RESOURCE_ID,AUTHORIZATION_ID,AUTHORITY", unique = true)})
@DynamicUpdate
@org.hibernate.annotations.Table(comment = "人员与（资源、权限）关系表", appliesTo = "Y9_ORG_PERSONS_RESOURCES")
@NoArgsConstructor
@Data
public class Y9PersonToResource extends Y9IdentityToResourceBase {

    private static final long serialVersionUID = -8527781135976550912L;

    /** 身份(人员)唯一标识 */
    @Column(name = "PERSON_ID", length = 38, nullable = false)
    @Comment("身份(人员)唯一标识")
    private String personId;

}
