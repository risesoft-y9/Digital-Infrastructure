package net.risesoft.permission.aop.advice;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.annotation.AnnotationUtils;

import net.risesoft.api.permission.PersonRoleApi;
import net.risesoft.api.permission.PositionRoleApi;
import net.risesoft.enums.IdentityEnum;
import net.risesoft.enums.LogicalEnum;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.permission.annotation.HasRoles;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * 是否拥有角色的 BeforeAdvice
 *
 * @author shidaobang
 * @date 2022/11/10
 */
public class HasRolesAdvice implements MethodBeforeAdvice {

    private final PersonRoleApi personRoleApi;
    private final PositionRoleApi positionRoleApi;

    public HasRolesAdvice(PersonRoleApi personRoleApi, PositionRoleApi positionRoleApi) {
        this.personRoleApi = personRoleApi;
        this.positionRoleApi = positionRoleApi;
    }

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        HasRoles hasRoles = AnnotationUtils.findAnnotation(method, HasRoles.class);
        if (hasRoles != null && hasRoles.value() != null) {
            String[] customIds = hasRoles.value();
            LogicalEnum logical = hasRoles.logical();
            IdentityEnum identity = hasRoles.identity();

            if (IdentityEnum.PERSON.equals(identity) && LogicalEnum.AND.equals(logical)) {
                checkAllPersonRoles(customIds);
                return;
            }

            if (IdentityEnum.PERSON.equals(identity) && LogicalEnum.OR.equals(logical)) {
                checkAnyPersonRoles(customIds);
                return;
            }

            if (IdentityEnum.POSITION.equals(identity) && LogicalEnum.AND.equals(logical)) {
                checkAllPositionRoles(customIds);
                return;
            }

            if (IdentityEnum.POSITION.equals(identity) && LogicalEnum.OR.equals(logical)) {
                checkAnyPositionRoles(customIds);
                return;
            }
        }

    }

    private void checkAnyPositionRoles(String[] customIds) {
        for (String customId : customIds) {
            if (hasPositionRole(customId)) {
                return;
            }
        }

        throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.POSITION_NOT_HAS_ROLE,
            Arrays.toString(customIds));
    }

    private boolean hasPositionRole(String customId) {
        return positionRoleApi
            .hasRoleByCustomId(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), customId).getData();
    }

    private void checkAllPositionRoles(String[] customIds) {
        for (String customId : customIds) {
            if (!hasPositionRole(customId)) {
                throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.POSITION_NOT_HAS_ROLE, customId);
            }
        }
    }

    private void checkAnyPersonRoles(String[] customIds) {
        for (String customId : customIds) {
            if (hasPersonRole(customId)) {
                return;
            }
        }
        // 所有都不满足才抛出异常
        throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.PERSON_NOT_HAS_ROLE, Arrays.toString(customIds));
    }

    private void checkAllPersonRoles(String[] customIds) {
        if (customIds != null && customIds.length > 0) {
            for (String customId : customIds) {
                // 只要有一个不满足就抛出异常
                if (!hasPersonRole(customId)) {
                    throw Y9ExceptionUtil.permissionException(GlobalErrorCodeEnum.PERSON_NOT_HAS_ROLE, customId);
                }
            }
        }
    }

    private boolean hasPersonRole(String customId) {
        return personRoleApi
            .hasRoleByCustomId(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), customId).getData();
    }

}
