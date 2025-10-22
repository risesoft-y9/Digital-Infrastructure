package net.risesoft.manager.org.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.manager.org.Y9DepartmentPropManager;
import net.risesoft.repository.org.Y9DepartmentPropRepository;

/**
 * 部门属性 Manager 实现类
 *
 * @author shidaobang
 * @date 2024/03/14
 */
@Service
@RequiredArgsConstructor
public class Y9DepartmentPropManagerImpl implements Y9DepartmentPropManager {

    private final Y9DepartmentPropRepository y9DepartmentPropRepository;

    @Override
    public void deleteByOrgUnitId(String orgUnitId) {
        y9DepartmentPropRepository.deleteByOrgBaseId(orgUnitId);
    }
}
