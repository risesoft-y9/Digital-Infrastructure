package org.apereo.cas.services.y9;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

@Entity(name = Y9LoginUser.ENTITY_NAME)
@Table(name = "Y9_LOGIN_USER")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@Slf4j
@Accessors(chain = true)
public class Y9LoginUser implements Serializable {
    /**
     * Th JPA entity name.
     */
    public static final String ENTITY_NAME = "Y9LoginUser";

    private static final long serialVersionUID = -6476891120156676097L;

    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键")
    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreationTimestamp
    @Column(name = "LOGIN_TIME", updatable = false)
    @Comment("登录日期")
    private Date loginTime;

    @Column(name = "LOGIN_TYPE", length = 255)
    @Comment("登录类型")
    private String loginType;

    @Column(name = "USER_ID", length = 255)
    @Comment("用户ID")
    private String userId;

    @Column(name = "USER_NAME", length = 255)
    @Comment("用户名称")
    private String userName;

    @Column(name = "USER_LOGIN_NAME", length = 255)
    @Comment("用户登录名称")
    private String userLoginName;

    @Column(name = "USER_HOST_IP", length = 255)
    @Comment("用户主机IP")
    private String userHostIp;

    @Column(name = "USER_HOST_MAC", length = 255)
    @Comment("用户主机MAC")
    private String userHostMac;

    @Column(name = "USER_HOST_NAME", length = 255)
    @Comment("用户主机名称")
    private String userHostName;

    @Column(name = "USER_HOST_DISKID", length = 255)
    @Comment("用户主机磁盘ID")
    private String userHostDiskId;

    @Column(name = "TENANT_ID", length = 255)
    @Comment("租户ID")
    private String tenantId;

    @Column(name = "TENANT_NAME", length = 255)
    @Comment("租户名称")
    private String tenantName;

    @Column(name = "SERVER_IP", length = 255)
    @Comment("服务器IP")
    private String serverIp;

    @Column(name = "SUCCESS", length = 255)
    @Comment("成功标志")
    private String success;

    @Column(name = "LOG_MESSAGE", length = 255)
    @Comment("日志信息")
    private String logMessage;

    @Column(name = "BROWSER_NAME", length = 255)
    @Comment("浏览器名称")
    private String browserName;

    @Column(name = "BROWSER_VERSION", length = 255)
    @Comment("浏览器版本")
    private String browserVersion;

    @Column(name = "OS_NAME", length = 255)
    @Comment("OS名称")
    private String osName;

    @Column(name = "SCREEN_RESOLUTION", length = 255)
    @Comment("用户主机屏幕分辨率")
    private String screenResolution;

    @Column(name = "CLIENT_IP_SECTION", length = 50)
    @Comment("IP地址区间段")
    private String clientIpSection;

    @Column(name = "MANAGER_LEVEL", nullable = false)
    @Comment("管理员类型")
    @ColumnDefault("0")
    private String managerLevel;
}
