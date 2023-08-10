package net.risesoft.y9public.entity.event;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.base.BaseEntity;

/**
 * 事件监听api获取记录表
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Entity
@Table(name = "Y9_EVENT_SYNC_HISTORY")
@Comment("事件监听api获取记录表")
@NoArgsConstructor
@Data
public class Y9PublishedEventSyncHistory extends BaseEntity {

    private static final long serialVersionUID = 3842435660802364598L;

    /** 主键 */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    /** 租户id */
    @Column(name = "TENANT_ID", length = 38, nullable = false)
    @Comment("租户id")
    private String tenantId;

    /** 应用名称事件操作者 */
    @Column(name = "APP_NAME")
    @Comment("应用名称事件操作者")
    private String appName;

    /** 上一次同步时间 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("上一次同步时间")
    @Column(name = "LAST_SYNC_TIME")
    private Date lastSyncTime;

}
