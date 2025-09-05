package net.risesoft.log.domain;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人员登录ip记录表
 *
 * @author mengjuhua
 * @date 2024/04/23
 */
@NoArgsConstructor
@Data
public class Y9LogUserHostIpInfoDO implements Serializable {
    private static final long serialVersionUID = -6096173983030412296L;

    /** 主键，唯一标识 */
    private String id;

    /** 登录用户机器IP */
    private String userHostIp;

    /** clientIp的ABC段 ip的前三位，如ip:192.168.1.114,则clientIp4ABC为192.168.1 */
    private String clientIpSection;

}
