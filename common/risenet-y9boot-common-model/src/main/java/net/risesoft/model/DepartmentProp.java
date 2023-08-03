package net.risesoft.model;

import java.io.Serializable;

import lombok.Data;

import net.risesoft.enums.Y9DepartmentPropCategoryEnum;

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
    private String deptId;

    /**
     * 组织唯一标示
     */
    private String orgBaseId;

    /**
     * 类别
     * 
     * {@link Y9DepartmentPropCategoryEnum}
     */
    private Integer category;

    /**
     * 序号
     */
    private Integer tabIndex;
}