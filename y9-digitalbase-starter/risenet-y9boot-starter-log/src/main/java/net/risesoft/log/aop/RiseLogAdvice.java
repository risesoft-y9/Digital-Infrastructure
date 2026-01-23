package net.risesoft.log.aop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.Y9LogContext;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.log.service.AccessLogReporter;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

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

    private final AccessLogReporter accessLogReporter;

    private static final Pattern SPEL_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");
    private final SpelExpressionParser parser = new SpelExpressionParser();

    public RiseLogAdvice(AccessLogReporter accessLogReporter) {
        this.accessLogReporter = accessLogReporter;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long start = System.nanoTime();
        String success = "成功";
        String errorMessage = "";
        String throwable = "";

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
            boolean isReportedLog = reportLog(invocation, start, success, errorMessage, throwable);

            if (isReportedLog) {
                ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                if (sra != null) {
                    HttpServletResponse response = sra.getResponse();
                    if (response != null) {
                        // Y9CommonFilter 见到这个标志后，就不再记录日志了，因为这里已经写了日志，不需要重复写。
                        response.setHeader("y9aoplog", "true");
                    }
                }
            }
            Y9LogContext.clear();
        }
        return ret;
    }

    private boolean reportLog(MethodInvocation invocation, long start, String success, String errorMessage,
                              String throwable) {
        try {
            Method method = invocation.getMethod();
            RiseLog riseLog = method.getAnnotation(RiseLog.class);

            if (riseLog != null && !riseLog.enable()) {
                // 只有 @RiseLog(enable = false) 不记录日志，其他情况都记录
                return false;
            }

            AccessLog log = new AccessLog();
            long end = System.nanoTime();
            long elapsedTime = end - start;

            ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (sra != null) {
                HttpServletRequest request = sra.getRequest();
                log.setUserAgent(request.getHeader("User-Agent"));
                log.setUserHostIp(Y9Context.getIpAddr(request));
            }
            log.setSystemName(Y9Context.getSystemName());
            log.setLogLevel(LogLevelEnum.RSLOG.toString());
            log.setLogTime(new Date());
            log.setMethodName(method.getDeclaringClass().getName() + "." + method.getName());
            log.setElapsedTime(elapsedTime);
            log.setSuccess(success);
            log.setLogMessage(errorMessage);
            log.setThrowable(throwable);
            log.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            log.setServerIp(Y9Context.getHostIp());

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

            log.setModularName(method.getDeclaringClass().getName());
            log.setOperateName(method.getName());
            log.setLogLevel(LogLevelEnum.RSLOG.toString());
            log.setOperateType(OperationTypeEnum.BROWSE.getValue());
            if (riseLog != null) {
                String moduleName;
                // 方法注解上没有设置 moduleName，则取类注解的 moduleName
                if (StringUtils.hasText(riseLog.moduleName())) {
                    moduleName = riseLog.moduleName();
                } else {
                    RiseLog classLevelRiseLog =
                        AnnotationUtils.findAnnotation(method.getDeclaringClass(), RiseLog.class);
                    moduleName = Optional.ofNullable(classLevelRiseLog).map(RiseLog::moduleName).orElse(null);
                }
                if (StringUtils.hasText(moduleName)) {
                    log.setModularName(moduleName);
                }

                String operationNameTemplate = riseLog.operationName();
                if (StringUtils.hasText(operationNameTemplate)) {
                    String operationName =
                        resolveSpringEl(operationNameTemplate, invocation.getMethod(), invocation.getArguments());
                    log.setOperateName(operationName);
                }

                if (riseLog.logLevel() != null) {
                    log.setLogLevel(riseLog.logLevel().toString());
                }

                log.setOperateType(riseLog.operationType().getValue());

                if (riseLog.saveParams()) {
                    Object[] arguments = invocation.getArguments();
                    Parameter[] parameters = method.getParameters();
                    Map<String, Object> keyValues = new HashMap<>();
                    for (int i = 0; i < parameters.length; i++) {
                        Object arg = arguments[i];
                        if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse
                            || arg instanceof MultipartFile) {
                            continue;
                        }
                        keyValues.put(parameters[i].getName(), arg);
                    }
                    log.setParamsJson(Y9JsonUtil.writeValueAsString(keyValues));
                }

            }

            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            if (null != userInfo) {
                log.setUserId(userInfo.getPersonId());
                log.setUserName(userInfo.getName());
                log.setLoginName(userInfo.getLoginName());
                log.setTenantId(userInfo.getTenantId());
                log.setTenantName(Y9LoginUserHolder.getTenantName());
                log.setDn(userInfo.getDn());
                log.setGuidPath(userInfo.getGuidPath());
                log.setManagerLevel(userInfo.getManagerLevel().getValue());
            }

            accessLogReporter.report(log);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return true;
    }

    private String resolveSpringEl(String templateString, Method method, Object[] arguments) {
        try {
            MethodBasedEvaluationContext context =
                new MethodBasedEvaluationContext(null, method, arguments, new DefaultParameterNameDiscoverer());
            // 添加日志上下文设置的变量
            context.setVariables(Y9LogContext.getMap());

            Matcher matcher = SPEL_PATTERN.matcher(templateString);
            StringBuffer stringBuffer = new StringBuffer();
            while (matcher.find()) {
                // 完整匹配
                // String functionName = matcher.group(0);
                // 中间内容部分
                String expressionStr = matcher.group(1);
                Expression expression = parser.parseExpression(expressionStr);
                String value = expression.getValue(context, String.class);
                matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(value));
            }
            matcher.appendTail(stringBuffer);

            return stringBuffer.toString();
        } catch (Exception e) {
            LOGGER.debug("SpringEl 表达式解析失败", e);
        }
        // 不能正确解析，原样返回
        return templateString;
    }

}
