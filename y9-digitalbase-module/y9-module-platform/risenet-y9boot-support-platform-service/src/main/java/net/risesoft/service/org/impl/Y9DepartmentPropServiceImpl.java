package net.risesoft.service.org.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9DepartmentProp;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.Y9DepartmentPropRepository;
import net.risesoft.service.org.Y9DepartmentPropService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9DepartmentPropServiceImpl implements Y9DepartmentPropService {

    private final Y9DepartmentPropRepository y9DepartmentPropRepository;

    @Override
    @Transactional(readOnly = false)
    public void deleteByDeptId(String deptId) {
        y9DepartmentPropRepository.deleteByDeptId(deptId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteById(String id) {
        y9DepartmentPropRepository.deleteById(id);
    }

    @Override
    public Optional<Y9DepartmentProp> findById(String id) {
        return y9DepartmentPropRepository.findById(id);
    }

    @Override
    public List<Y9DepartmentProp> listAll() {
        return y9DepartmentPropRepository.findAll();
    }

    @Override
    public List<Y9DepartmentProp> listByCategory(DepartmentPropCategoryEnum category) {
        return y9DepartmentPropRepository.findByCategoryOrderByTabIndex(category);
    }

    @Override
    public List<Y9DepartmentProp> listByDeptId(String deptId) {
        return y9DepartmentPropRepository.findByDeptId(deptId);
    }

    @Override
    public List<Y9DepartmentProp> listByDeptIdAndCategory(String deptId, DepartmentPropCategoryEnum category) {
        return y9DepartmentPropRepository.findByDeptIdAndCategoryOrderByTabIndex(deptId, category);
    }

    @Override
    public List<Y9DepartmentProp> listByOrgBaseIdAndCategory(String orgBaseId, DepartmentPropCategoryEnum category) {
        return y9DepartmentPropRepository.findByOrgBaseIdAndCategoryOrderByTabIndex(orgBaseId, category);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrUpdate(Y9DepartmentProp y9DepartmentProp) {
        String id = y9DepartmentProp.getId();
        Y9DepartmentProp prop = null;
        if (StringUtils.isNotEmpty(id)) {
            prop = this.findById(id).orElse(null);
        } else {
            id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        }
        if (null == prop) {
            prop = new Y9DepartmentProp();
            prop.setId(id);
        }
        prop.setDeptId(y9DepartmentProp.getDeptId());
        prop.setOrgBaseId(y9DepartmentProp.getOrgBaseId());
        prop.setCategory(y9DepartmentProp.getCategory());
        Integer tabIndex =
            y9DepartmentPropRepository.getMaxTabIndex(y9DepartmentProp.getDeptId(), y9DepartmentProp.getCategory());
        if (null == tabIndex) {
            tabIndex = 1;
        } else {
            tabIndex++;
        }
        prop.setTabIndex(tabIndex);
        y9DepartmentPropRepository.save(prop);
    }

}
