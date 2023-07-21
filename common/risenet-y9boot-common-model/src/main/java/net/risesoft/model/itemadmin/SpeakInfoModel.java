package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class SpeakInfoModel implements Serializable {

    private static final long serialVersionUID = 9176582654675102844L;

    /**
     * 唯一标示
     */
    private String id;

    /**
     * 流程实例Id
     */
    private String processInstanceId;

    /**
     * 发言信息
     */
    private String content;

    /**
     * 发言人名称
     */
    private String userName;

    /**
     * 发言人Id
     */
    private String userId;

    /**
     * 是否可以编辑
     */
    private boolean edited;

    /**
     * 发言时间
     */
    private String createTime;

    /**
     * 发言时间
     */
    private String updateTime;

}
