package net.risesoft.service.org.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9DepartmentProp;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.enums.platform.org.DepartmentPropCategoryEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.org.Y9DepartmentPropRepository;
import net.risesoft.service.org.Y9DepartmentPropService;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;

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
        return y9DepartmentPropRepository.findByCategoryOrderByTabIndex(category.getValue());
    }

    @Override
    public List<Y9DepartmentProp> listByDeptId(String deptId) {
        return y9DepartmentPropRepository.findByDeptId(deptId);
    }

    @Override
    public List<Y9DepartmentProp> listByDeptIdAndCategory(String deptId, DepartmentPropCategoryEnum category) {
        return y9DepartmentPropRepository.findByDeptIdAndCategoryOrderByTabIndex(deptId, category.getValue());
    }

    @Override
    public List<Y9DepartmentProp> listByOrgBaseIdAndCategory(String orgBaseId, DepartmentPropCategoryEnum category) {
        return y9DepartmentPropRepository.findByOrgBaseIdAndCategoryOrderByTabIndex(orgBaseId, category.getValue());
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
            y9DepartmentPropRepository.getMaxTabIndex(y9DepartmentProp.getDeptId(), y9DepartmentProp.getCategory())
                .orElse(1);
        prop.setTabIndex(++tabIndex);
        y9DepartmentPropRepository.save(prop);
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department department = event.getEntity();
        y9DepartmentPropRepository.deleteByDeptId(department.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onPersonDeleted(Y9EntityDeletedEvent<Y9Person> event) {
        Y9Person person = event.getEntity();
        y9DepartmentPropRepository.deleteByOrgBaseId(person.getId());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onPositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position position = event.getEntity();
        y9DepartmentPropRepository.deleteByOrgBaseId(position.getId());
    }
}
