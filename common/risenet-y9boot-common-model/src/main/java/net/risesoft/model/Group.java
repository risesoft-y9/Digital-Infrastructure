package net.risesoft.model;

import lombok.Data;

/**
 * 用户组
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class Group extends OrgUnit {

    private static final long serialVersionUID = 1095290600377937717L;

    /**
     * 岗位组或者用户组
     */
    private String type;

}