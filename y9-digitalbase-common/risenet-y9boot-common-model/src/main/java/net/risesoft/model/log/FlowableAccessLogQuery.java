package net.risesoft.model.log;

import lombok.Data;

/**
 * 工作流日志查询
 *
 * @author shidaobang
 * @date 2025/12/08
 */
@Data
public class FlowableAccessLogQuery extends AccessLogQuery {

    private static final long serialVersionUID = -267224062237610318L;

    /**
     * 标题
     */
    private String title;

    /**
     * 请求参数及值json字符串
     */
    private String arguments;

}
