package net.risesoft.permission.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.permission.annotation.RepeatSubmit;
import net.risesoft.permission.cache.SubmitCache;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

/**
 * 防重复提交 Advice
 *
 * @author shidaobang
 * @date 2025/07/21
 */
@RequiredArgsConstructor
@Slf4j
public class RepeatSubmitAdvice implements MethodBeforeAdvice {
    public final String REPEAT_REQUEST_PREFIX = "repeatRequest";

    private final SubmitCache submitCache;

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        Annotation annotation = AnnotationUtils.findAnnotation(method, RepeatSubmit.class);
        if (annotation != null) {
            RepeatSubmit repeatSubmit = (RepeatSubmit)annotation;
            ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            if (isRepeatSubmit(request, args, repeatSubmit)) {
                // Y9Result result = Y9Result.failure(repeatSubmit.message());
                // Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(result));
                throw Y9ExceptionUtil.businessException(GlobalErrorCodeEnum.REPEAT_REQUEST_DENIED);
                // throw new RuntimeException("不允许重复提交，请稍后再试");
            }
        }
    }

    public boolean isRepeatSubmit(HttpServletRequest request, Object[] args, RepeatSubmit annotation) {
        String ipAddr = Y9Context.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String personId = Y9LoginUserHolder.getPersonId();
        String params = Y9JsonUtil.writeValueAsString(args);
        String url = request.getRequestURI();
        String requestSha = Y9MessageDigestUtil.sha256(ipAddr + userAgent + personId + params + url);

        String cacheKey =
            StringUtils.join(new Object[] {REPEAT_REQUEST_PREFIX, Y9Context.getSystemName(), requestSha}, ":");
        LOGGER.trace("cacheKey:{}", cacheKey);

        String value = submitCache.get(cacheKey);
        LOGGER.trace("value:{}", value);

        if (value != null) {
            // 如果缓存中存在，说明是重复提交
            return true;
        }

        submitCache.put(cacheKey, String.valueOf(annotation.interval()), annotation.interval());
        return false;
    }

}
