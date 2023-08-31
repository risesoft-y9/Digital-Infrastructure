package net.risesoft.manager.authorization.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.manager.authorization.Y9PersonToRoleManager;
import net.risesoft.repository.identity.person.Y9PersonToRoleRepository;
import net.risesoft.y9public.entity.role.Y9Role;

/**
 * 人员角色关联 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9PersonToRoleManagerImpl implements Y9PersonToRoleManager {

    private final Y9PersonToRoleRepository y9PersonToRoleRepository;

    @Override
    @Transactional(readOnly = false)
    public void update(Y9Person y9Person, List<Y9Role> personRelatedY9RoleList) {
        removeInvalid(y9Person.getId(), personRelatedY9RoleList);

        for (Y9Role y9Role : personRelatedY9RoleList) {
            Optional<Y9PersonToRole> personToRoleOptional =
                y9PersonToRoleRepository.findByPersonIdAndRoleId(y9Person.getId(), y9Role.getId());
            if (personToRoleOptional.isEmpty()) {
                this.save(y9Person, y9Role);
            }
        }
    }

    @Transactional(readOnly = false)
    public Y9PersonToRole save(Y9Person person, Y9Role role) {
        Y9PersonToRole matrix = new Y9PersonToRole();
        matrix.setTenantId(person.getTenantId());
        matrix.setPersonId(person.getId());
        matrix.setDepartmentId(person.getParentId());
        matrix.setRoleId(role.getId());
        matrix.setRoleName(role.getName());
        matrix.setSystemCnName(role.getSystemCnName());
        matrix.setSystemName(role.getSystemName());
        matrix.setAppName(role.getAppCnName());
        matrix.setAppId(role.getAppId());
        matrix.setDescription(role.getDescription());
        return y9PersonToRoleRepository.save(matrix);
    }

    /**
     * 移除失效的关联记录（即在最新计算的角色中不再包含的关联记录）
     *
     * @param personId 人员id
     * @param newCalculatedY9RoleList 最新计算的角色列表
     */
    @Transactional(readOnly = false)
    public void removeInvalid(String personId, List<Y9Role> newCalculatedY9RoleList) {
        List<String> originY9RoleIdList = this.listRoleIdsByPersonId(personId);
        List<String> newCalculatedY9RoleIdList =
            newCalculatedY9RoleList.stream().map(Y9Role::getId).collect(Collectors.toList());
        for (String roleId : originY9RoleIdList) {
            if (!newCalculatedY9RoleIdList.contains(roleId)) {
                this.removeByPersonIdAndRoleId(personId, roleId);
            }
        }
    }

    @Override
    public List<String> listRoleIdsByPersonId(String personId) {
        List<Y9PersonToRole> personRoleMappings = y9PersonToRoleRepository.findByPersonId(personId);
        return personRoleMappings.stream().map(Y9PersonToRole::getRoleId).collect(Collectors.toList());
    }

    @Transactional(readOnly = false)
    public void removeByPersonIdAndRoleId(String personId, String roleId) {
        Optional<Y9PersonToRole> y9PersonToRoleOptional =
            y9PersonToRoleRepository.findByPersonIdAndRoleId(personId, roleId);
        if (y9PersonToRoleOptional.isPresent()) {
            y9PersonToRoleRepository.deleteById(y9PersonToRoleOptional.get().getId());
        }
    }

}
