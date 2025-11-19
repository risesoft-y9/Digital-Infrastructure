package net.risesoft.service.org.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9DepartmentProp;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.enums.platform.org.DepartmentPropCategoryEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.DepartmentProp;
import net.risesoft.repository.org.Y9DepartmentPropRepository;
import net.risesoft.service.org.Y9DepartmentPropService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Y9DepartmentPropServiceImpl implements Y9DepartmentPropService {

    private final Y9DepartmentPropRepository y9DepartmentPropRepository;

    @Override
    @Transactional
    public void deleteById(String id) {
        y9DepartmentPropRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByDeptIdAndCategoryAndOrgBaseId(String deptId, DepartmentPropCategoryEnum category,
        String orgBaseId) {
        y9DepartmentPropRepository.deleteByDeptIdAndCategoryAndOrgBaseId(deptId, category.getValue(), orgBaseId);
    }

    private Optional<Y9DepartmentProp> findById(String id) {
        return y9DepartmentPropRepository.findById(id);
    }

    @Override
    public List<DepartmentProp> listByDeptId(String deptId) {
        return entityToModel(y9DepartmentPropRepository.findByDeptId(deptId));
    }

    @Override
    public List<DepartmentProp> listByDeptIdAndCategory(String deptId, DepartmentPropCategoryEnum category) {
        List<Y9DepartmentProp> y9DepartmentPropList =
            y9DepartmentPropRepository.findByDeptIdAndCategoryOrderByTabIndex(deptId, category.getValue());
        return entityToModel(y9DepartmentPropList);
    }

    @Override
    public List<DepartmentProp> listByOrgBaseIdAndCategory(String orgBaseId, DepartmentPropCategoryEnum category) {
        List<Y9DepartmentProp> y9DepartmentPropList =
            y9DepartmentPropRepository.findByOrgBaseIdAndCategoryOrderByTabIndex(orgBaseId, category.getValue());
        return entityToModel(y9DepartmentPropList);
    }

    @Override
    @Transactional
    public void saveOrUpdate(DepartmentProp y9DepartmentProp) {
        Optional<Y9DepartmentProp> optionalY9DepartmentProp =
            y9DepartmentPropRepository.findByDeptIdAndOrgBaseIdAndCategory(y9DepartmentProp.getDeptId(),
                y9DepartmentProp.getOrgBaseId(), y9DepartmentProp.getCategory());
        if (optionalY9DepartmentProp.isEmpty()) {
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
            prop.setTabIndex(getNextTabIndex(y9DepartmentProp));
            y9DepartmentPropRepository.save(prop);
        }
    }

    private Integer getNextTabIndex(DepartmentProp y9DepartmentProp) {
        return y9DepartmentPropRepository.getMaxTabIndex(y9DepartmentProp.getDeptId(), y9DepartmentProp.getCategory())
            .orElse(-1) + 1;
    }

    @EventListener
    @Transactional
    public void onDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department department = event.getEntity();
        y9DepartmentPropRepository.deleteByDeptId(department.getId());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除部门，同步删除部门信息配置执行完成！");
        }
    }

    @EventListener
    @Transactional
    public void onPersonDeleted(Y9EntityDeletedEvent<Y9Person> event) {
        Y9Person person = event.getEntity();
        y9DepartmentPropRepository.deleteByOrgBaseId(person.getId());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除人员时其下部门信息配置同步删除执行完成！");
        }
    }

    @EventListener
    @Transactional
    public void onPositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position position = event.getEntity();
        y9DepartmentPropRepository.deleteByOrgBaseId(position.getId());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("删除岗位时其下部门信息配置同步删除执行完成！");
        }
    }

    private List<DepartmentProp> entityToModel(List<Y9DepartmentProp> y9DepartmentPropList) {
        return PlatformModelConvertUtil.convert(y9DepartmentPropList, DepartmentProp.class);
    }
}
