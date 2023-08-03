package net.risesoft.permission.aop.advice;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.annotation.AnnotationUtils;

import net.risesoft.api.permission.PersonResourceApi;
import net.risesoft.api.permission.PositionResourceApi;
import net.risesoft.enums.IdentityEnum;
import net.risesoft.enums.LogicalEnum;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.permission.annotation.HasAuthorities;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * 是否拥有权限的 BeforeAdvice
 *
 * @author shidaobang
 * @date 2022/11/11
 */
public class HasAuthoritiesAdvice implements MethodBeforeAdvice {

    private final PersonResourceApi personResourceApi;
    private final PositionResourceApi positionResourceApi;

    public HasAuthoritiesAdvice(PersonResourceApi personResourceApi, PositionResourceApi positionResourceApi) {
        this.personResourceApi = personResourceApi;
        this.positionResourceApi = positionResourceApi;
    }

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        HasAuthorities hasAuthorities = AnnotationUtils.findAnnotation(method, HasAuthorities.class);
        if (hasAuthorities != null && hasAuthorities.value() != null) {
            String[] customIds = hasAuthorities.value();
            Integer authority = hasAuthorities.authority().getValue();
            LogicalEnum logical = hasAuthorities.logical();
            IdentityEnum identity = hasAuthorities.identity();

            if (IdentityEnum.PERSON.equals(identity) && LogicalEnum.AND.equals(logical)) {
                checkAllPersonPermission(customIds, authority);
                return;
            }

            if (IdentityEnum.PERSON.equals(identity) && LogicalEnum.OR.equals(logical)) {
                checkAnyPersonPermission(customIds, authority);
                return;
            }

            if (IdentityEnum.POSITION.equals(identity) && LogicalEnum.AND.equals(logical)) {
                checkAllPositionPermission(customIds, authority);
                return;
            }

            if (IdentityEnum.POSITION.equals(identity) && LogicalEnum.OR.equals(logical)) {
                checkAnyPositionPermission(customIds, authority);
                return;
            }

        }
    }

    private void checkAllPersonPermission(String[] customIds, Integer authority) {
        for (String customId : customIds) {
            if (!hasPersonPermission(customId, authority)) {
                throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.PERSON_UNAUTHORIZED_RESOURCE, customId);
            }
        }
    }

    private void checkAnyPersonPermission(String[] customIds, Integer authority) {
        for (String customId : customIds) {
            if (hasPersonPermission(customId, authority)) {
                return;
            }
        }
        throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.PERSON_UNAUTHORIZED_RESOURCE,
            Arrays.toString(customIds));
    }

    private void checkAllPositionPermission(String[] customIds, Integer authority) {
        for (String customId : customIds) {
            if (!hasPersonPermission(customId, authority)) {
                throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.POSITION_UNAUTHORIZED_RESOURCE, customId);
            }
        }
    }

    private void checkAnyPositionPermission(String[] customIds, Integer authority) {
        for (String customId : customIds) {
            if (hasPositionPermission(customId, authority)) {
                return;
            }
        }
        throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.POSITION_UNAUTHORIZED_RESOURCE,
            Arrays.toString(customIds));
    }

    private boolean hasPersonPermission(String customId, Integer authority) {
        return personResourceApi.hasPermissionByCustomId(Y9LoginUserHolder.getTenantId(),
            Y9LoginUserHolder.getPersonId(), customId, authority);
    }

    private boolean hasPositionPermission(String customId, Integer authority) {
        return positionResourceApi.hasPermissionByCustomId(Y9LoginUserHolder.getTenantId(),
            Y9LoginUserHolder.getPositionId(), customId, authority);
    }

}
