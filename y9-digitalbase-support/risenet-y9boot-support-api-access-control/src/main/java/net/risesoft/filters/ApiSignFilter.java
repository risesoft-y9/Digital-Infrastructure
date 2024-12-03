package net.risesoft.filters;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import lombok.RequiredArgsConstructor;

import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.ApiSignUtil;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.Y9ApiAccessControl;
import net.risesoft.y9public.service.Y9ApiAccessControlService;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;

/**
 * api 签名过滤器 <br>
 * 用于防篡改 一定程度的防重放
 *
 * @author shidaobang
 * @date 2024/11/27
 */
@RequiredArgsConstructor
public class ApiSignFilter implements Filter {

    private final static String APP_ID_HEADER = "x-app-id";
    private final static String TIMESTAMP_HEADER = "x-timestamp";
    private final static String SIGNATURE_HEADER = "x-signature";

    private final Y9ApiAccessControlService y9ApiAccessControlService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;

        try {
            String appId = httpServletRequest.getHeader(APP_ID_HEADER);
            String requestTimestamp = httpServletRequest.getHeader(TIMESTAMP_HEADER);
            String requestSignature = httpServletRequest.getHeader(SIGNATURE_HEADER);

            String currentTimestamp = String.valueOf(System.currentTimeMillis());

            // 验证请求头是否完整
            if (StringUtils.isBlank(appId) || StringUtils.isBlank(requestTimestamp)
                || StringUtils.isBlank(requestSignature)) {
                throw Y9ExceptionUtil.businessException(GlobalErrorCodeEnum.API_SIGN_HEADERS_INCOMPLETE);
            }

            String requestURI = httpServletRequest.getRequestURI();
            String queryString = httpServletRequest.getQueryString();
            String body = IoUtil.read(httpServletRequest.getReader());

            Y9ApiAccessControl y9ApiAccessControl = y9ApiAccessControlService.getById(appId);
            String appSecret = y9ApiAccessControl.getValue();
            String calculatedSignature =
                ApiSignUtil.sign(appId, appSecret, requestURI, queryString, body, requestTimestamp);

            // 验证签名是否正确
            if (!StringUtils.equalsIgnoreCase(requestSignature, calculatedSignature)) {
                throw Y9ExceptionUtil.businessException(GlobalErrorCodeEnum.API_SIGN_INCORRECT);
            }

            // 防一定程度的重放攻击 需保证客户端和服务端之间时间同步
            if (DateUtil.between(new Date(Long.parseLong(requestTimestamp)), new Date(Long.parseLong(currentTimestamp)),
                DateUnit.SECOND) > 60) {
                throw Y9ExceptionUtil.businessException(GlobalErrorCodeEnum.API_SIGN_TIMESTAMP_INVALID);
            }

            chain.doFilter(request, response);
        } catch (Y9BusinessException e) {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.getWriter().write(Y9JsonUtil.writeValueAsString(Y9Result.failure(e.getMessage())));
            return;
        }

    }
}
