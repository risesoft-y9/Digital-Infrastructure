package net.risesoft.model.platform.dictionary;

import javax.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.model.BaseModel;

/**
 * 字典类型
 *
 * @author shidaobang
 * @date 2025/10/30
 */
@Data
public class OptionClass extends BaseModel {

    private static final long serialVersionUID = 8838072859443610243L;

    /** 主键，类型名称 */
    @NotBlank
    private String type;

    /** 中文名称 */
    @NotBlank
    private String name;

}