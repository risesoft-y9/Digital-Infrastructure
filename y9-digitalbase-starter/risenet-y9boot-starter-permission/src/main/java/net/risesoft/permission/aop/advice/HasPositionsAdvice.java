package net.risesoft.permission.aop.advice;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.util.StringUtils;

import net.risesoft.enums.LogicalEnum;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.model.user.UserInfo;
import net.risesoft.permission.annotation.HasPositions;
import net.risesoft.permission.aop.PermissionAnnotationResolver;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * 是否拥有岗位的 BeforeAdvice
 *
 * @author shidaobang
 * @date 2022/11/11
 */
public class HasPositionsAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        HasPositions hasPositions =
            PermissionAnnotationResolver.findAnnotationForInvocation(method, target, HasPositions.class);
        if (hasPositions == null) {
            return;
        }

        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String positions = userInfo != null ? userInfo.getPositions() : null;
        List<String> userPositions = Arrays.asList(StringUtils.tokenizeToStringArray(positions, ","));
        List<String> requiredPositions = Arrays.asList(hasPositions.value());
        boolean allowed = hasPositions.logical() == LogicalEnum.AND ? userPositions.containsAll(requiredPositions)
            : !Collections.disjoint(userPositions, requiredPositions);
        if (!allowed) {
            throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.PERMISSION_DENIED);
        }
    }

}
