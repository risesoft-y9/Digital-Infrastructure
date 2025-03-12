package net.risesoft.filters;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.useronline.Y9UserOnlineProperties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9.util.RemoteCallUtil;

/**
 * 用户在线过滤器
 *
 * @author shidaobang
 * @date 2025/03/06
 */
@Slf4j
public class Y9UserOnlineFilter implements Filter {

    private final KafkaTemplate<String, Object> y9KafkaTemplate;
    private final Y9UserOnlineProperties y9UserOnlineProperties;
    private final Y9Properties y9Properties;

    public Y9UserOnlineFilter(Y9Properties y9Properties, Y9UserOnlineProperties y9UserOnlineProperties,
        KafkaTemplate<String, Object> y9KafkaTemplate) {
        this.y9Properties = y9Properties;
        this.y9UserOnlineProperties = y9UserOnlineProperties;
        this.y9KafkaTemplate = y9KafkaTemplate;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();

        if (userInfo != null && y9UserOnlineProperties.isEnabled()) {
            remoteSaveUserOnline(userInfo);
        }

        filterChain.doFilter(request, response);
    }

    private void remoteSaveUserOnline(UserInfo userInfo) {
        try {
            if (Objects.equals(y9UserOnlineProperties.getReportMethod(), Y9UserOnlineProperties.ReportMethod.KAFKA)) {
                String jsonString = Y9JsonUtil.writeValueAsString(userInfo);
                if (this.y9KafkaTemplate != null) {
                    this.y9KafkaTemplate.send(Y9TopicConst.Y9_USERONLINE_MESSAGE, jsonString);
                }
            } else if (Objects.equals(y9UserOnlineProperties.getReportMethod(),
                Y9UserOnlineProperties.ReportMethod.API)) {
                String userOnlineBaseUrl = y9Properties.getCommon().getUserOnlineBaseUrl();
                String saveOnlineUrl = userOnlineBaseUrl + "/services/rest/userOnline/saveAsync";
                List<NameValuePair> requestBody = RemoteCallUtil.objectToNameValuePairList(userInfo);
                RemoteCallUtil.post(saveOnlineUrl, null, requestBody, Object.class);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

}
