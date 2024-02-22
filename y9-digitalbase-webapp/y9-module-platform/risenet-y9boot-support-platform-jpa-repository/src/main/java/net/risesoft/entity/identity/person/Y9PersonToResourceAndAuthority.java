package net.risesoft.entity.identity.person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.identity.Y9IdentityToResourceAndAuthorityBase;

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
@Comment("人员与（资源、权限）关系表")
@NoArgsConstructor
@Data
public class Y9PersonToResourceAndAuthority extends Y9IdentityToResourceAndAuthorityBase {

    private static final long serialVersionUID = -8527781135976550912L;

    /** 身份(人员)唯一标识 */
    @Column(name = "PERSON_ID", length = 38, nullable = false)
    @Comment("身份(人员)唯一标识")
    private String personId;

}
