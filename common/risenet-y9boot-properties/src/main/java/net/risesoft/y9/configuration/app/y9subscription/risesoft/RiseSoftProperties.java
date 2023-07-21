package net.risesoft.y9.configuration.app.y9subscription.risesoft;

import lombok.Getter;
import lombok.Setter;

/**
 * 有生软件
 */
@Getter
@Setter
public class RiseSoftProperties {

    private String name = "有生软件";
    private String tenantId = "c425281829dc4d4496ddddf7fc0198d0";

    /**
     * 合作申请发送人登录名
     */
    private String cooperationApplySenderLoginName = "admin";
    /**
     * 合作申请接收人对应的角色id
     */
    private String cooperationApplyReceiverRoleId = "570044c7db3f4d349d8e05ff7caeaf02";

}
