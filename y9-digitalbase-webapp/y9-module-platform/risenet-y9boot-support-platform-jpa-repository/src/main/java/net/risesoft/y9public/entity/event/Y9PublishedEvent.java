package net.risesoft.y9public.entity.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 事件信息表
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_PUBLISHED_EVENT")
@org.hibernate.annotations.Table(comment = "事件信息表", appliesTo = "Y9_PUBLISHED_EVENT")
@NoArgsConstructor
@Data
public class Y9PublishedEvent extends BaseEntity {

    private static final long serialVersionUID = 6579436882467488027L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /** 租户id */
    @Column(name = "TENANT_ID", length = 38, nullable = true)
    @Comment("租户id")
    private String tenantId;

    /** 事件类型 */
    @Column(name = "EVENT_TYPE", length = 255, nullable = false)
    @Comment("事件类型")
    private String eventType;

    /** 事件类型名称 */
    @Column(name = "EVENT_NAME", length = 255, nullable = false)
    @Comment("事件类型名称")
    private String eventName;

    /** 事件处理对象id */
    @Column(name = "OBJ_ID", length = 255, nullable = true)
    @Comment("事件处理对象id")
    private String objId;

    /** 事件操作者 */
    @Column(name = "OPERATOR", nullable = false)
    @Comment("事件操作者")
    private String operator;

    /** 操作者的客户端ip */
    @Column(name = "CLIENT_IP", nullable = false)
    @Comment("操作者的客户端ip")
    private String clientIp;

    /** 具体事件描述 */
    @Column(name = "EVENT_DESCRIPTION", nullable = false)
    @Comment("具体事件描述")
    private String eventDescription;

    /** 事件处理对象实体类信息 */
    @Lob
    @Column(name = "ENTITY_JSON")
    @Comment("事件处理对象实体类信息")
    private String entityJson;

}
