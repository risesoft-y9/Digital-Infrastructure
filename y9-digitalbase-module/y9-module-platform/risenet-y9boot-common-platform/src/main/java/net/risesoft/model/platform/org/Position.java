package net.risesoft.model.platform.org;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

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
     * 职位id
     */
    @NotBlank
    private String jobId;

    /**
     * 职位名称
     */
    private String JobName;

    /**
     * 排序序列号
     */
    private String orderedPath;

    /**
     * 岗位容量，默认容量为1，即一人一岗
     */
    private Integer capacity;

    /**
     * 岗位当前人数，小于或等于岗位容量
     */
    private Integer headCount;

}
