package net.risesoft.model.platform.org;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 人员与组关联
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class PersonsGroups implements Serializable {

    private static final long serialVersionUID = 2899191697188889119L;

    /** 主键 */
    private String id;

    /** 用户组id */
    @NotBlank
    private String groupId;

    /** 人员ID */
    @NotBlank
    private String personId;

    /** 用户组排序号 */
    private Integer groupOrder;

    /** 人员排序号 */
    private Integer personOrder;

}