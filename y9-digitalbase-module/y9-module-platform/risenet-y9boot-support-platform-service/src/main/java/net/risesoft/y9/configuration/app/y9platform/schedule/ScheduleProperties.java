package net.risesoft.y9.configuration.app.y9platform.schedule;

import lombok.Getter;
import lombok.Setter;

/**
 * 定时任务属性
 *
 * @author shidaobang
 * @date 2026/07/03
 */
@Getter
@Setter
public class ScheduleProperties {

    /**
     * 检查三员审查情况 cron 每天 01:00:00 执行
     */
    private String checkManagerLogReviewCron = "0 0 1 * * ?";

    /**
     * 检查三员密码修改情况 cron 每天 01:00:00 执行
     */
    private String checkManagerPasswordModificationCron = "0 0 1 * * ?";

    /**
     * 同步授权主体的资源权限 cron 每天 02:00:00 执行
     */
    private String syncIdentityResourceCron = "0 0 2 * * ?";

    /**
     * 同步授权主体的角色 cron 每天 04:00:00 执行
     */
    private String syncIdentityRoleCron = "0 0 4 * * ?";

}
