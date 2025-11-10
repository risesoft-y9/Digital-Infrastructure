package net.risesoft.model.platform.org;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.enums.platform.org.DepartmentPropCategoryEnum;

/**
 * 部门信息配置表
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Data
public class DepartmentProp implements Serializable {

    private static final long serialVersionUID = 1239795207430901518L;

    /**
     * 唯一标示
     */
    private String id;

    /**
     * 部门唯一标示
     */
    @NotBlank
    private String deptId;

    /**
     * 组织唯一标示
     */
    @NotBlank
    private String orgBaseId;

    /**
     * 类别
     * 
     * {@link DepartmentPropCategoryEnum}
     */
    @NotBlank
    private Integer category;

    /**
     * 序号
     */
    private Integer tabIndex;
}