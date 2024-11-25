package net.risesoft.log.aop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.log.service.AccessLogPusher;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.InetAddressUtil;

/**
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 */
@DependsOn("y9Context")
@Slf4j
public class RiseLogAdvice implements MethodInterceptor {

    private final AccessLogPusher accessLogPusher;
    private final String serverIp;

    public RiseLogAdvice(AccessLogPusher accessLogPusher) {
        this.accessLogPusher = accessLogPusher;
        this.serverIp = InetAddressUtil.getLocalAddress().getHostAddress();
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long start = System.nanoTime();
        String errorMessage = "";
        String throwable = "";
        String success = "成功";
        String userAgent = "";
        String hostIp = "";
        String systemName = "";

        Object ret = null;
        try {
            ret = invocation.proceed();
        } catch (Exception e) {
            success = "出错";
            errorMessage = e.getMessage();

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            // throwable = stringWriter.toString().replace("\r\n", "<br/>");
            throwable = stringWriter.toString();
            throw e;
        } finally {
            Method method = invocation.getMethod();
            RiseLog riseLog = method.getAnnotation(RiseLog.class);
            HttpServletResponse response = null;
            try {
                ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                if (sra != null) {
                    HttpServletRequest request = sra.getRequest();
                    response = sra.getResponse();
                    userAgent = request.getHeader("User-Agent");
                    hostIp = Y9Context.getIpAddr(request);
                }
                systemName = Y9Context.getSystemName();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }

            if (riseLog != null && riseLog.enable()) {
                long end = System.nanoTime();
                long elapsedTime = end - start;

                AccessLog log = new AccessLog();
                try {
                    log.setLogLevel(LogLevelEnum.RSLOG.toString());
                    log.setLogTime(new Date());
                    log.setMethodName(method.getDeclaringClass().getName() + "." + method.getName());
                    log.setElapsedTime(String.valueOf(elapsedTime));
                    log.setSuccess(success);
                    log.setLogMessage(errorMessage);
                    log.setThrowable(throwable);
                    log.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    log.setServerIp(this.serverIp);
                    log.setUserHostIp(hostIp);
                    log.setUserAgent(userAgent);
                    log.setSystemName(systemName);

                    Map<String, Object> map = Y9LoginUserHolder.getMap();
                    if (map != null) {
                        String userHostIp = (String)map.get("userHostIP");
                        if (userHostIp != null) {
                            log.setUserHostIp(userHostIp);
                        }
                        String requestUrl = (String)map.get("requestURL");
                        if (requestUrl != null) {
                            log.setRequestUrl(requestUrl);
                        }
                    }

                    if (StringUtils.hasText(riseLog.moduleName())) {
                        log.setModularName(riseLog.moduleName());
                    } else {
                        log.setModularName("数字底座");
                    }
                    if (StringUtils.hasText(riseLog.operationName())) {
                        log.setOperateName(riseLog.operationName());
                    } else {
                        log.setOperateName(method.getName());
                    }
                    if (null != riseLog.logLevel()) {
                        log.setLogLevel(riseLog.logLevel().toString());
                    }

                    log.setOperateType(riseLog.operationType().getValue());

                    UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
                    if (null != userInfo) {
                        log.setUserId(userInfo.getPersonId());
                        log.setUserName(userInfo.getName());
                        log.setLoginName(userInfo.getLoginName());
                        log.setTenantId(userInfo.getTenantId());
                        log.setTenantName(Y9LoginUserHolder.getTenantName());
                        log.setDn(userInfo.getDn());
                        log.setGuidPath(userInfo.getGuidPath());
                        log.setManagerLevel(String.valueOf(userInfo.getManagerLevel().getValue()));
                    }

                    accessLogPusher.push(log);
                    if (response != null) {
                        // Y9CommonFilter 见到这个标志后，就不再记录日志了，因为这里已经写了日志，不需要重复写。
                        response.addHeader("y9aoplog", "true");
                    }

                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            response.addHeader("y9aoplog", "true");
        }

        return ret;
    }

}
