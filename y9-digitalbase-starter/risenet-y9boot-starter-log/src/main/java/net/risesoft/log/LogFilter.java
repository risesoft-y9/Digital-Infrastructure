package net.risesoft.log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.service.AccessLogReporter;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 日志过滤器 <br>
 * 仅当方法上没有{@link net.risesoft.log.annotation.RiseLog}注解记录日志时，这个过滤器才会记录，避免重复记录
 *
 * @author shidaobang
 * @date 2024/11/14
 * @since 9.6.8
 */
@Slf4j
@RequiredArgsConstructor
public class LogFilter implements Filter {

    private final AccessLogReporter accessLogReporter;
    private final String serverIp;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        long startTime = System.nanoTime();
        String success = "成功";
        String errorMessage = "";
        String throwable = "";

        try {
            chain.doFilter(servletRequest, response);
        } catch (Exception e) {
            success = "出错";
            errorMessage = e.getMessage();
            throwable = buildExceptionMessage(e);
        } finally {
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            String y9aoplog = response.getHeader("y9aoplog");
            if (!"true".equals(y9aoplog)) {
                remoteSaveLog(request, Y9LoginUserHolder.getUserInfo(), elapsedTime, success, errorMessage, throwable);
            }
        }

    }

    private String buildExceptionMessage(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    private void remoteSaveLog(HttpServletRequest request, UserInfo userInfo, long elapsedTime, String success,
        String errorMessage, String throwable) {
        String url = request.getRequestURL().toString();

        if (url.endsWith(".js") || url.endsWith(".css") || url.endsWith(".gif") || url.endsWith(".jpg")
            || url.endsWith(".png") || url.endsWith(".svg")) {
            return;
        }

        try {
            AccessLog log = new AccessLog();
            log.setLogLevel("RSLOG");
            log.setLogTime(new Date());
            log.setRequestUrl(url);
            // log.setMethodName(url);
            log.setElapsedTime(elapsedTime);
            log.setSuccess(success);
            log.setLogMessage(errorMessage);
            log.setThrowable(throwable);
            log.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            log.setServerIp(this.serverIp);
            log.setUserHostIp(Y9Context.getIpAddr(request));
            // log.setModularName();
            // log.setOperateName("");
            log.setOperateType("活动");
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setSystemName(Y9Context.getSystemName());
            if (userInfo != null) {
                log.setUserId(userInfo.getParentId());
                log.setUserName(userInfo.getLoginName());
                log.setTenantId(userInfo.getTenantId());
                log.setTenantName(userInfo.getTenantName());
                log.setGuidPath(userInfo.getGuidPath());
                log.setManagerLevel(userInfo.getManagerLevel().getValue());
            }
            accessLogReporter.report(log);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
