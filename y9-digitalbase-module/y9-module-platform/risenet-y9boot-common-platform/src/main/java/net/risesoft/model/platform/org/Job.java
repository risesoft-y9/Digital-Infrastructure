package net.risesoft.model.platform.org;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.model.BaseModel;

/**
 * 职位
 *
 * @author shidaobang
 * @date 2022/10/17
 */
@Data
public class Job extends BaseModel {

    private static final long serialVersionUID = 1878774546924910882L;

    /** 主键id */
    private String id;

    /** 数据代码 */
    @NotBlank
    private String code;

    /** 名称 */
    @NotBlank
    private String name;

    /** 排序号 */
    private Integer tabIndex;

}
