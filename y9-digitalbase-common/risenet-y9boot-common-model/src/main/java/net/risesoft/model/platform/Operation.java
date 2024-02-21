package net.risesoft.model.platform;

import lombok.Data;

import net.risesoft.enums.OperationDisplayTypeEnum;

/**
 * 页面按钮操作资源
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class Operation extends Resource {

    private static final long serialVersionUID = 8337074625810329535L;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 展示方式
     */
    private OperationDisplayTypeEnum displayType;
}
