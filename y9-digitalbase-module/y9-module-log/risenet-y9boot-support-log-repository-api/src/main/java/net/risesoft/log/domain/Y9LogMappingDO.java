package net.risesoft.log.domain;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模块名称映射表
 *
 * @author mengjuhua
 * @date 2024/04/23
 */
@NoArgsConstructor
@Data
public class Y9LogMappingDO implements Serializable {
    private static final long serialVersionUID = -290275690477972055L;

    /** 主键，唯一标识 */
    private String id;

    /** 模块名称，比如：公文就转-发文-授权管理 */
    private String modularName;

    /**
     * 模块中文名称
     */
    private String modularCnName;
}
