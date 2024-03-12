package net.risesoft.service.identity.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.manager.identity.Y9PersonToRoleManager;
import net.risesoft.manager.identity.Y9PositionToRoleManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.service.identity.IdentityRoleCalculator;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToGroupsService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.role.Y9RoleManager;

/**
 * 身份角色计算实现
 *
 * @author shidaobang
 * @date 2024/03/08
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class IdentityRoleCalculatorImpl implements IdentityRoleCalculator {

    private final CompositeOrgBaseService compositeOrgBaseService;

    private final Y9PersonService y9PersonService;
    private final Y9PositionService y9PositionService;
    private final Y9PersonsToGroupsService y9PersonsToGroupsService;
    private final Y9PersonsToPositionsService y9PersonsToPositionsService;

    private final Y9PersonManager y9PersonManager;
    private final Y9RoleManager y9RoleManager;
    private final Y9PositionManager y9PositionManager;

    private final Y9PersonToRoleManager y9PersonToRoleManager;
    private final Y9PositionToRoleManager y9PositionToRoleManager;

    @Override
    public void recalculateByOrgUnitId(final String orgUnitId) {
        Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(orgUnitId);
        if (y9OrgBase != null && y9OrgBase.getId() != null) {
            OrgTypeEnum orgType = y9OrgBase.getOrgType();
            switch (orgType) {
                case ORGANIZATION:
                    List<String> personIdList = y9PersonService.findIdByGuidPathStartingWith(y9OrgBase.getGuidPath());
                    for (String personId : personIdList) {
                        this.recalculateByPersonId(personId);
                    }
                    List<String> positionIdList =
                        y9PositionService.findIdByGuidPathStartingWith(y9OrgBase.getGuidPath());
                    for (String positionId : positionIdList) {
                        this.recalculateByPositionId(positionId);
                    }
                    break;
                case DEPARTMENT:
                    List<String> personIds = y9PersonService.findIdByGuidPathStartingWith(y9OrgBase.getGuidPath());
                    for (String personId : personIds) {
                        this.recalculateByPersonId(personId);
                    }
                    List<String> positionIds = y9PositionService.findIdByGuidPathStartingWith(y9OrgBase.getGuidPath());
                    for (String positionId : positionIds) {
                        this.recalculateByPositionId(positionId);
                    }
                    break;
                case GROUP:
                    List<Y9PersonsToGroups> y9PersonsToGroups = y9PersonsToGroupsService.findByGroupId(orgUnitId);
                    for (Y9PersonsToGroups orgPersonsGroup : y9PersonsToGroups) {
                        String orgPersonId = orgPersonsGroup.getPersonId();
                        this.recalculateByPersonId(orgPersonId);
                    }
                    break;
                case POSITION:
                    this.recalculateByPositionId(orgUnitId);
                    // 人员的角色还包括关联岗位的角色
                    List<Y9PersonsToPositions> y9PersonsToPositionsList =
                        y9PersonsToPositionsService.findByPositionId(orgUnitId);
                    for (Y9PersonsToPositions y9PersonsToPositions : y9PersonsToPositionsList) {
                        this.recalculateByPersonId(y9PersonsToPositions.getPersonId());
                    }
                    break;
                case PERSON:
                    this.recalculateByPersonId(orgUnitId);
                    break;
                default:
            }
        }
    }

    private void recalculate(Y9Person y9Person, List<Y9Role> personRelatedY9RoleList) {
        removeInvalidByPersonId(y9Person.getId(), personRelatedY9RoleList);

        for (Y9Role y9Role : personRelatedY9RoleList) {
            y9PersonToRoleManager.save(y9Person, y9Role);
        }
    }

    /**
     * 移除失效的关联记录（即在最新计算的角色中不再包含的关联记录）
     *
     * @param personId 人员id
     * @param newCalculatedY9RoleList 最新计算的角色列表
     */
    private void removeInvalidByPersonId(String personId, List<Y9Role> newCalculatedY9RoleList) {
        List<String> originY9RoleIdList = y9PersonToRoleManager.findRoleIdByPersonId(personId);
        List<String> newCalculatedY9RoleIdList =
            newCalculatedY9RoleList.stream().map(Y9Role::getId).collect(Collectors.toList());
        for (String roleId : originY9RoleIdList) {
            if (!newCalculatedY9RoleIdList.contains(roleId)) {
                y9PersonToRoleManager.removeByPersonIdAndRoleId(personId, roleId);
            }
        }
    }

    @Override
    public void recalculateByPersonId(String personId) {
        try {
            Optional<Y9Person> y9PersonOptional = y9PersonManager.findById(personId);
            if (y9PersonOptional.isPresent()) {
                Y9Person y9Person = y9PersonOptional.get();
                List<Y9Role> personRelatedY9RoleList =
                    y9RoleManager.listOrgUnitRelatedWithoutNegative(y9Person.getId());
                this.recalculate(y9Person, personRelatedY9RoleList);
            }
        } catch (Exception e) {
            LOGGER.warn("计算人员[{}]角色发生异常", personId, e);
        }
    }

    private void recalculateByPositionId(String positionId) {
        try {
            List<Y9Role> positionRelatedY9RoleList = y9RoleManager.listOrgUnitRelatedWithoutNegative(positionId);
            Y9Position y9Position = y9PositionManager.getById(positionId);
            this.recalculate(y9Position, positionRelatedY9RoleList);
        } catch (Exception e) {
            LOGGER.warn("计算岗位[{}]角色发生异常", positionId, e);
        }
    }

    private void recalculate(Y9Position y9Position, List<Y9Role> positionRelatedY9RoleList) {
        removeInvalidByPositionId(y9Position.getId(), positionRelatedY9RoleList);
        for (Y9Role y9Role : positionRelatedY9RoleList) {
            y9PositionToRoleManager.save(y9Position, y9Role);
        }
    }

    /**
     * 移除失效的关联记录（即在最新计算的角色中不再包含的关联记录）
     *
     * @param positionId 人员id
     * @param newCalculatedY9RoleList 最新计算的角色列表
     */
    private void removeInvalidByPositionId(String positionId, List<Y9Role> newCalculatedY9RoleList) {
        List<String> originY9RoleIdList = y9PositionToRoleManager.listRoleIdByPositionId(positionId);
        List<String> newCalculatedY9RoleIdList =
            newCalculatedY9RoleList.stream().map(Y9Role::getId).collect(Collectors.toList());
        for (String roleId : originY9RoleIdList) {
            if (!newCalculatedY9RoleIdList.contains(roleId)) {
                y9PositionToRoleManager.deleteByPositionIdAndRoleId(positionId, roleId);
            }
        }
    }

}
