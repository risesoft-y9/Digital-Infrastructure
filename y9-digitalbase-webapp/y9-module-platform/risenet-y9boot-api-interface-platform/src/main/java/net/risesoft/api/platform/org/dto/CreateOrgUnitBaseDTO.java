package net.risesoft.api.platform.org.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

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
    protected String id;

    /** 是否可用 */
    protected Boolean disabled = false;

    /** 描述 */
    protected String description;

    /** 自定义id */
    protected String customId;

    /** 名称 */
    @NotBlank
    protected String name;

    /** 扩展属性 */
    protected String properties;

    /** 排序号 */
    protected Integer tabIndex = 0;

}
