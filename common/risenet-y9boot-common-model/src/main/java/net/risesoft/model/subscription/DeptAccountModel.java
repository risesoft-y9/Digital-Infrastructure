package net.risesoft.model.subscription;

import java.io.Serializable;

import lombok.Data;

/**
 * 部门号
 *
 * @author shidaobang
 * @date 2022/12/29
 */
@Data
public class DeptAccountModel implements Serializable {

    private static final long serialVersionUID = -2760665403045906386L;

    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 部门号logo
     */
    private String pic;

}
