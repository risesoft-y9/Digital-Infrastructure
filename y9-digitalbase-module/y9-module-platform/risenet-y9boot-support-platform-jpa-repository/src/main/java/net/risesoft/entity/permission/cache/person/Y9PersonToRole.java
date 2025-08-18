package net.risesoft.entity.permission.cache.person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.permission.cache.Y9IdentityToRoleBase;

/**
 * 人员与角色关系表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_ORG_PERSONS_ROLES", indexes = {@Index(columnList = "PERSON_ID,ROLE_ID", unique = true)})
@DynamicUpdate
@Comment("人员与角色关系表")
@NoArgsConstructor
@Data
public class Y9PersonToRole extends Y9IdentityToRoleBase {

    private static final long serialVersionUID = -8527781135976550912L;

    /** 身份(人员)唯一标识 */
    @Column(name = "PERSON_ID", length = 38, nullable = false)
    @Comment("身份(人员)唯一标识")
    private String personId;

}
