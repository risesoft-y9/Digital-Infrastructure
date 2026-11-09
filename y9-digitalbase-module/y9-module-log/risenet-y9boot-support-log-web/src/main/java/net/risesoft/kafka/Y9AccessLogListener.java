package net.risesoft.kafka;

import java.util.Date;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.domain.Y9LogAccessLogDO;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.Y9MessageListener;
import net.risesoft.y9.pubsub.Y9ReceivedMessage;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9public.service.Y9logAccessLogService;

@Slf4j
@RequiredArgsConstructor
public class Y9AccessLogListener implements Y9MessageListener {

    private final Y9logAccessLogService y9logAccessLogService;

    @Override
    public String topic() {
        return Y9TopicConst.Y9_ACCESSLOG_MESSAGE;
    }

    @Override
    public void onMessage(Y9ReceivedMessage data) {
        try {
            Y9LogAccessLogDO y9LogAccessLogDO = Y9JsonUtil.readValue(data.getPayload(), Y9LogAccessLogDO.class);
            y9LogAccessLogDO.setLogTime(new Date());
            if (y9LogAccessLogDO.getUserHostIp().contains(":")) {
                y9LogAccessLogDO.setUserHostIp("127.0.0.1");
            }
            y9logAccessLogService.save(y9LogAccessLogDO);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
