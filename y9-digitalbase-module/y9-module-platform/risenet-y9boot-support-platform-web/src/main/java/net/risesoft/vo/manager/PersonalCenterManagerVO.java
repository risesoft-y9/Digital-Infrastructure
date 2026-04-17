package net.risesoft.vo.manager;

import java.io.Serializable;

import lombok.Data;

import net.risesoft.model.platform.org.Manager;


/**
 * 个人中心管理员 vo
 *
 * @author shidaobang
 * @date 2026/04/16
 */
@Data
public class PersonalCenterManagerVO implements Serializable {

    private static final long serialVersionUID = 7239471878560526994L;

    /** 管理员信息 */
    private Manager manager;

    /** 密码修改周期 */
    private Integer passwordModifiedCycle;

    /** 日志审核周期 */
    private Integer reviewLogCycle;

    /** 下次密码修改时间 */
    private String nextModifyPwdTime;

    /** 下次日志审核时间 */
    private String nextCheckTime;

}
