package net.risesoft.manager.permission.cache.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.permission.cache.person.Y9PersonToRole;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.permission.cache.Y9PersonToRoleManager;
import net.risesoft.repository.permission.cache.person.Y9PersonToRoleRepository;
import net.risesoft.y9public.entity.Y9Role;

/**
 * 人员角色关联 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@RequiredArgsConstructor
public class Y9PersonToRoleManagerImpl implements Y9PersonToRoleManager {

    private final Y9PersonToRoleRepository y9PersonToRoleRepository;

    @Override
    public List<String> findRoleIdByPersonId(String personId) {
        return y9PersonToRoleRepository.findRoleIdByPersonId(personId);
    }

    @Override
    public void removeByPersonIdAndRoleId(String personId, String roleId) {
        Optional<Y9PersonToRole> y9PersonToRoleOptional =
            y9PersonToRoleRepository.findByPersonIdAndRoleId(personId, roleId);
        if (y9PersonToRoleOptional.isPresent()) {
            y9PersonToRoleRepository.deleteById(y9PersonToRoleOptional.get().getId());
        }
    }

    @Override
    public void save(Y9Person person, Y9Role role) {
        Optional<Y9PersonToRole> personToRoleOptional =
            y9PersonToRoleRepository.findByPersonIdAndRoleId(person.getId(), role.getId());
        if (personToRoleOptional.isEmpty()) {
            Y9PersonToRole y9PersonToRole = new Y9PersonToRole();
            y9PersonToRole.setId(Y9IdGenerator.genId());
            y9PersonToRole.setTenantId(person.getTenantId());
            y9PersonToRole.setPersonId(person.getId());
            y9PersonToRole.setRoleId(role.getId());
            y9PersonToRole.setAppId(role.getAppId());
            y9PersonToRole.setSystemId(role.getSystemId());
            y9PersonToRoleRepository.save(y9PersonToRole);
        } else {
            Y9PersonToRole y9PersonToRole = personToRoleOptional.get();
            y9PersonToRole.setAppId(role.getAppId());
            y9PersonToRole.setSystemId(role.getSystemId());
            y9PersonToRoleRepository.save(y9PersonToRole);
        }
    }

}
