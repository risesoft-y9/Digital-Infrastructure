package net.risesoft.model.platform.org;

import javax.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.model.BaseModel;

/**
 * 岗位与组关联
 *
 * @author shidaobang
 * @date 2025/11/06
 */
@Data
public class PositionsGroups extends BaseModel {

    private static final long serialVersionUID = 2899191697180789119L;

    /** 主键 */
    private String id;

    /** 用户组id */
    @NotBlank
    private String groupId;

    /** 岗位ID */
    @NotBlank
    private String positionId;

    /** 用户组排序号 */
    private Integer groupOrder;

    /** 同一个用户组的岗位排序号 */
    private Integer positionOrder;

}