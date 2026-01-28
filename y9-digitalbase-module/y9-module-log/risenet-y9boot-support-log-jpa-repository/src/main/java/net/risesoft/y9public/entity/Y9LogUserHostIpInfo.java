package net.risesoft.y9public.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人员登录ip记录表
 *
 * @author mengjuhua
 * @date 2024/04/23
 */
@Entity
@Table(name = "Y9_LOG_USER_HOSTIP_INFO", comment = "人员登录ip记录表")
@NoArgsConstructor
@Data
public class Y9LogUserHostIpInfo implements Serializable {
    private static final long serialVersionUID = -6096173983030412296L;

    /** 主键，唯一标识 */
    @Id
    @Column(name = "ID", comment = "主键")
    private String id;

    /** 登录用户机器IP */
    @Column(name = "USER_HOST_IP", length = 50, comment = "登录用户机器IP")
    private String userHostIp;

    /** clientIp的ABC段 */
    @Column(name = "CLIENT_IP_SECTION", length = 50, comment = "ip的前三位")
    //@Comment(value = "ip的前三位，如ip:192.168.1.114,则clientIp4ABC为192.168.1")
    private String clientIpSection;

}
