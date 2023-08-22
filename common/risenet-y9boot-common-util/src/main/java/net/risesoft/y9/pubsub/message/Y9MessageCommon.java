package net.risesoft.y9.pubsub.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用消息
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Y9MessageCommon implements Serializable {
    private static final long serialVersionUID = -1107265840539410276L;

    /** 解决 jackson 序列化丢失类型 */
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private Serializable eventObject;
    private String eventType;
}