package net.risesoft.y9.pubsub.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务消息
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Y9MessageTask implements Serializable {

    private static final long serialVersionUID = -4044715647192713249L;

    // 解决 jackson 序列化丢失类型
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private Serializable taskObj;
    private String eventType;
    private String tenantId;
    private String personId;
}