package net.risesoft.model.platform.org;

import javax.validation.constraints.NotBlank;

import lombok.Data;

import net.risesoft.model.BaseModel;

/**
 * 人员与岗位关联
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class PersonsPositions extends BaseModel {

    private static final long serialVersionUID = 8926134052486805025L;

    /** 主键 */
    private String id;

    /** 岗位ID */
    @NotBlank
    private String positionId;

    /** 人员ID */
    @NotBlank
    private String personId;

    /** 岗位排序号 */
    private Integer positionOrder;

    /** 人员排序号 */
    private Integer personOrder;

}