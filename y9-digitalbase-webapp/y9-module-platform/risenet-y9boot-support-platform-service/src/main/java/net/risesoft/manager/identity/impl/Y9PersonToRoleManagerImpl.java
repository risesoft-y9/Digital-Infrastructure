package net.risesoft.manager.identity.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.identity.Y9PersonToRoleManager;
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void save(Y9Person person, Y9Role role) {
        Optional<Y9PersonToRole> personToRoleOptional =
            y9PersonToRoleRepository.findByPersonIdAndRoleId(person.getId(), role.getId());
        if (personToRoleOptional.isEmpty()) {
            Y9PersonToRole matrix = new Y9PersonToRole();
            matrix.setId(Y9IdGenerator.genId());
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

            y9PersonToRoleRepository.save(matrix);
        }
    }

    @Override
    public List<String> findRoleIdByPersonId(String personId) {
        return y9PersonToRoleRepository.findRoleIdByPersonId(personId);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeByPersonIdAndRoleId(String personId, String roleId) {
        Optional<Y9PersonToRole> y9PersonToRoleOptional =
            y9PersonToRoleRepository.findByPersonIdAndRoleId(personId, roleId);
        if (y9PersonToRoleOptional.isPresent()) {
            y9PersonToRoleRepository.deleteById(y9PersonToRoleOptional.get().getId());
        }
    }

}
