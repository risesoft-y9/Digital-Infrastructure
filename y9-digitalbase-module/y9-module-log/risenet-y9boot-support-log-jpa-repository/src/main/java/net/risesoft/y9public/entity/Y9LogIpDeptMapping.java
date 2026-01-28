package net.risesoft.y9public.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人员登录部门配置记录表
 *
 * @author mengjuhua
 * @date 2024/04/23
 */
@Entity
@Table(name = "Y9_LOG_IP_DEPT_MAPPING", comment = "人员登录部门配置记录表")
@NoArgsConstructor
@Data
public class Y9LogIpDeptMapping implements Serializable {
    private static final long serialVersionUID = -3758903946162468650L;

    /** 主键，唯一标识 */
    @Id
    @Column(name = "ID", comment = "主键")
    private String id;

    /** clientIp的ABC段 */
    @Column(name = "CLIENT_IP_SECTION", length = 50, nullable = false, comment = "ip的前三位")
    //@Comment(value = "ip的前三位，如ip:192.168.1.114,则clientIp4ABC为192.168.1")
    private String clientIpSection;

    /** 操作者 */
    @Column(name = "OPERTATOR", length = 50, nullable = false, comment = "操作者")
    private String operator;

    /** 部门名称 */
    @Column(name = "DEPT_NAME", length = 100, nullable = false, comment = "部门名称")
    private String deptName;

    /** 保存时间 */
    @Column(name = "SAVE_TIME", comment = "保存时间")
    private String saveTime;

    /** 修改时间 */
    @Column(name = "UPDATE_TIME", comment = "修改时间")
    private String updateTime;

    /** 排序号 */
    @Column(name = "TAB_INDEX", nullable = false, comment = "排序号")
    private Integer tabIndex;

    /** 状态，用于表示是否有有人在该终端登录，0：表示没有，1，表示有 */
    @Column(name = "STATUS", length = 10, comment = "终端登录状态")
    private Integer status;

    /** 租户ID */
    @Column(name = "TENANT_ID", length = 38, comment = "租户ID")
    private String tenantId;

}
