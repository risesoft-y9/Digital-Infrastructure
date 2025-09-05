package net.risesoft.log.domain;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人员登录部门配置记录表
 *
 * @author mengjuhua
 * @date 2024/04/23
 */
@NoArgsConstructor
@Data
public class Y9LogIpDeptMappingDO implements Serializable {
    private static final long serialVersionUID = -3758903946162468650L;

    /** 主键，唯一标识 */
    private String id;

    /** clientIp的ABC段 ip的前三位，如ip:192.168.1.114,则clientIp4ABC为192.168.1 */
    private String clientIpSection;

    /** 操作者 */
    private String operator;

    /** 部门名称 */
    private String deptName;

    /** 保存时间 */
    private String saveTime;

    /** 修改时间 */
    private String updateTime;

    /** 排序号 */
    private Integer tabIndex;

    /** 状态，用于表示是否有有人在该终端登录，0：表示没有，1，表示有 */
    private Integer status;

    /** 租户ID */
    private String tenantId;

}
