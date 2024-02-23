package net.risesoft.api.platform.org.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * @author shidaobang
 * @date 2023/11/06
 * @since 9.6.3
 */
@Getter
@Setter
public class CreatePositionDTO implements Serializable {

    private static final long serialVersionUID = -6444031768723849631L;

    /** 父节点id */
    @NotBlank
    private String parentId;

    /** 职位id */
    @NotBlank
    private String jobId;

    /** 岗位容量，默认容量为1，即一人一岗 */
    private Integer capacity = 1;

}
