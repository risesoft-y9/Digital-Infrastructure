package net.risesoft.model.platform;

import java.io.Serializable;

import lombok.Data;

/**
 * 人员岗位关联
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class PersonsPositions implements Serializable {

    private static final long serialVersionUID = 8926134052486805025L;

    /** 主键 */
    private String id;

    /** 岗位ID */
    private String positionId;

    /** 人员ID */
    private String personId;

    /** 岗位排序号 */
    private Integer positionOrder;

    /** 人员排序号 */
    private Integer personOrder;

}