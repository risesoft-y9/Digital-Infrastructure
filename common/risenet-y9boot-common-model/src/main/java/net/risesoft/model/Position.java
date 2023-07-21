package net.risesoft.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 岗位
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class Position extends OrgUnit implements Serializable {

    private static final long serialVersionUID = 1095290600488048828L;

    /**
     * 职务类型
     */
    private String dutyType;

    /**
     * 职级名称
     */
    private String dutyLevelName;

    /**
     * 职级
     */
    private Integer dutyLevel;

    /**
     * 职务
     */
    private String duty;

    /**
     * 类型
     */
    private String type;

    /**
     * 
     */
    private String orderedPath;

}
