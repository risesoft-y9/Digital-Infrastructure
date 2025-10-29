package net.risesoft.model.platform.org;

import lombok.Data;

import net.risesoft.enums.platform.org.GroupTypeEnum;

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
    private GroupTypeEnum type;

}