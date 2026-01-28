package net.risesoft.y9public.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模块名称映射表
 *
 * @author mengjuhua
 * @date 2024/04/23
 */
@Entity
@Table(name = "Y9_LOG_MAPPING", comment = "模块名称映射表")
@NoArgsConstructor
@Data
public class Y9LogMapping implements Serializable {
    private static final long serialVersionUID = -290275690477972055L;

    /** 主键，唯一标识 */
    @Id
    @Column(name = "ID", comment = "主键")
    private String id;

    /**
     * 模块名称
     */
    /** 模块名称，比如：公文就转-发文-授权管理 */
    @Column(name = "MODULAR_NAME", length = 200, nullable = false, comment = "模块名称")
    private String modularName;

    /**
     * 模块中文名称
     */
    @Column(name = "MODULAR_CN_NAME", length = 100, nullable = false, comment = "模块中文名称")
    private String modularCnName;
}
