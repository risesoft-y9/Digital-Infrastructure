package net.risesoft.model.subscription;

import java.io.Serializable;

import lombok.Data;

/**
 * 合作申请
 *
 * @author shidaobang
 */
@Data
public class CooperationApplyModel implements Serializable {

    private static final long serialVersionUID = -6294558108243112480L;

    /**
     * 公司 / 单位
     */
    private String company;

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 联系人电话
     */
    private String phone;

    /**
     * 联系人邮箱
     */
    private String email;

    /**
     * 合作需求
     */
    private String requirement;
}
