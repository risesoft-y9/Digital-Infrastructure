package net.risesoft.y9.configuration.app.y9email;

import org.springframework.util.unit.DataSize;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9EmailProperties {

    /**
     * 发送至统一待办
     */
    private boolean sendToTodo = false;

    /**
     * 发送微信提醒开关
     */
    private boolean weChatMsgEnable = false;

    /**
     * 微信提醒api
     */
    private String weChatMsgApi = "https://www.youshengyun.com/Youshengyun/Api/pushEmailMessage";

    /**
     * 超大附件功能启用
     */
    private boolean bigAttachmentEnable = false;

    /**
     * 超大附件大小
     */
    private DataSize bigAttachmentSize = DataSize.ofMegabytes(1000);

    /**
     * 超大附件过期天数
     */
    private int bigAttachmentExpireDays = 30;

}
