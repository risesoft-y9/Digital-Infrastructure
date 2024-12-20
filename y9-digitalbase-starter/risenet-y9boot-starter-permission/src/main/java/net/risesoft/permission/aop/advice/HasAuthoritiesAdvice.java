package net.risesoft.permission.aop.advice;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.annotation.AnnotationUtils;

import net.risesoft.api.platform.permission.PersonResourceApi;
import net.risesoft.api.platform.permission.PositionResourceApi;
import net.risesoft.enums.LogicalEnum;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.IdentityTypeEnum;
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
            AuthorityEnum authority = hasAuthorities.authority();
            LogicalEnum logical = hasAuthorities.logical();
            IdentityTypeEnum identity = hasAuthorities.identity();

            if (IdentityTypeEnum.PERSON.equals(identity) && LogicalEnum.AND.equals(logical)) {
                checkAllPersonPermission(customIds, authority);
                return;
            }

            if (IdentityTypeEnum.PERSON.equals(identity) && LogicalEnum.OR.equals(logical)) {
                checkAnyPersonPermission(customIds, authority);
                return;
            }

            if (IdentityTypeEnum.POSITION.equals(identity) && LogicalEnum.AND.equals(logical)) {
                checkAllPositionPermission(customIds, authority);
                return;
            }

            if (IdentityTypeEnum.POSITION.equals(identity) && LogicalEnum.OR.equals(logical)) {
                checkAnyPositionPermission(customIds, authority);
                return;
            }

        }
    }

    private void checkAllPersonPermission(String[] customIds, AuthorityEnum authority) {
        for (String customId : customIds) {
            if (!hasPersonPermission(customId, authority)) {
                throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.PERSON_UNAUTHORIZED_RESOURCE, customId);
            }
        }
    }

    private void checkAnyPersonPermission(String[] customIds, AuthorityEnum authority) {
        for (String customId : customIds) {
            if (hasPersonPermission(customId, authority)) {
                return;
            }
        }
        throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.PERSON_UNAUTHORIZED_RESOURCE,
            Arrays.toString(customIds));
    }

    private void checkAllPositionPermission(String[] customIds, AuthorityEnum authority) {
        for (String customId : customIds) {
            if (!hasPersonPermission(customId, authority)) {
                throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.POSITION_UNAUTHORIZED_RESOURCE, customId);
            }
        }
    }

    private void checkAnyPositionPermission(String[] customIds, AuthorityEnum authority) {
        for (String customId : customIds) {
            if (hasPositionPermission(customId, authority)) {
                return;
            }
        }
        throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.POSITION_UNAUTHORIZED_RESOURCE,
            Arrays.toString(customIds));
    }

    private boolean hasPersonPermission(String customId, AuthorityEnum authority) {
        return personResourceApi.hasPermissionByCustomId(Y9LoginUserHolder.getTenantId(),
            Y9LoginUserHolder.getPersonId(), customId, authority).getData();
    }

    private boolean hasPositionPermission(String customId, AuthorityEnum authority) {
        return positionResourceApi.hasPermissionByCustomId(Y9LoginUserHolder.getTenantId(),
            Y9LoginUserHolder.getPositionId(), customId, authority).getData();
    }

}
