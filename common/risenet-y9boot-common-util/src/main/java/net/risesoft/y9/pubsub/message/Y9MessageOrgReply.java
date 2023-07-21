package net.risesoft.y9.pubsub.message;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织消息的响应消息
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Y9MessageOrgReply implements Serializable {
    private static final long serialVersionUID = 1834756729435414044L;

    private String eventType;
    private String tenantId;
    private String clientIp;
    private String systemName;
}