package net.risesoft.api.platform.org.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.consts.DefaultConsts;

/**
 * @author shidaobang
 * @date 2023/11/03
 * @since 9.6.3
 */
@Getter
@Setter
public class CreateOrgUnitBaseDTO implements Serializable {

    private static final long serialVersionUID = -8361749786841530846L;

    /** ID */
    private String id;

    /** 是否可用 */
    private Boolean disabled = false;

    /** 描述 */
    private String description;

    /** 自定义id */
    private String customId;

    /** 名称 */
    @NotBlank
    private String name;

    /** 扩展属性 */
    private String properties;

    /** 排序号 */
    private Integer tabIndex = DefaultConsts.TAB_INDEX;

}
