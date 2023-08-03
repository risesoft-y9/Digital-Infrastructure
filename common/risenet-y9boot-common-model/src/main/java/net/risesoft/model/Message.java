package net.risesoft.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回消息
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@NoArgsConstructor
@Data
public class Message {

    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAIL = "fail";

    private String status;
    private String msg;

}