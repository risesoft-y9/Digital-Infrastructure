package net.risesoft.model.platform.org;

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

    /** 职位id */
    private String jobId;

    /** 职位名称 */
    private String JobName;

    /** 排序序列号 */
    private String orderedPath;

}
