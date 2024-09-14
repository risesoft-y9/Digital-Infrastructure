package net.risesoft.y9public.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用点击信息表
 *
 * @author mengjuhua
 * @date 2024/04/23
 */
@Entity
@Table(name = "Y9_LOG_CLICKED_APP")
@Comment("应用点击信息表")
@NoArgsConstructor
@Data
public class Y9ClickedApp implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 144334145599572308L;

    /** 主键，唯一标识 */
    @Id
    @Column(name = "ID")
    @Comment("主键")
    private String id;

    /** 用户ID */
    @Column(name = "PERSON_ID", length = 100, nullable = false)
    @Comment("用户ID ")
    private String personId;

    /** 租户ID */
    @Column(name = "TENANT_ID", length = 38, nullable = false)
    @Comment("租户ID ")
    private String tenantId;

    /** 应用ID */
    @Column(name = "APP_ID", length = 38, nullable = false)
    @Comment("应用ID ")
    private String appId;

    /** 应用名称 */
    @Column(name = "APP_NAME", length = 255)
    @Comment("应用名称 ")
    private String appName;

    /** 保存日期 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "SAVE_DATE")
    @Comment("保存日期 ")
    private Date saveDate;
}
