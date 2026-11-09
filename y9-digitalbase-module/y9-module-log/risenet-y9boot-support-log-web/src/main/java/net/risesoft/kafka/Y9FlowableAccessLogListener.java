package net.risesoft.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.domain.Y9LogFlowableAccessLogDO;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.Y9MessageListener;
import net.risesoft.y9.pubsub.Y9ReceivedMessage;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9public.service.Y9logFlowableAccessLogService;

/**
 * @author qinman
 */
@Slf4j
@RequiredArgsConstructor
public class Y9FlowableAccessLogListener implements Y9MessageListener {

    private final Y9logFlowableAccessLogService y9logFlowableAccessLogService;

    @Override
    public String topic() {
        return Y9TopicConst.Y9_ACCESSLOG_MESSAGE_FLOWABLE;
    }

    @Override
    public void onMessage(Y9ReceivedMessage data) {
        try {
            Y9LogFlowableAccessLogDO y9LogFlowableAccessLogDO =
                Y9JsonUtil.readValue(data.getPayload(), Y9LogFlowableAccessLogDO.class);
            y9logFlowableAccessLogService.save(y9LogFlowableAccessLogDO);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
