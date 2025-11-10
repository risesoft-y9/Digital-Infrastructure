package net.risesoft.model.platform.dictionary;

import javax.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.model.BaseModel;

/**
 * 字典数据
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class OptionValue extends BaseModel {

    private static final long serialVersionUID = -3091695448276608201L;

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 数据代码
     */
    @NotBlank
    private String code;

    /**
     * 名称
     */
    @NotBlank
    private String name;

    /**
     * 排序号
     */
    private Integer tabIndex;

    /**
     * 字典类型
     */
    @NotBlank
    private String type;
}
