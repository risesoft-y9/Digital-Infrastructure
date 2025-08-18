package net.risesoft.manager.org.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.consts.DefaultConsts;
import net.risesoft.entity.org.Y9Job;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9JobManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.repository.org.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;

@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.ORG_POSITION)
@RequiredArgsConstructor
public class Y9PositionManagerImpl implements Y9PositionManager {

    private final Y9PersonsToPositionsRepository y9PersonsToPositionsRepository;
    private final Y9PositionRepository y9PositionRepository;

    private final Y9JobManager y9JobManager;
    private final Y9PersonManager y9PersonManager;
    private final CompositeOrgBaseManager compositeOrgBaseManager;

    private final Y9SettingService y9SettingService;

    @Override
    public String buildName(Y9Job y9Job, List<Y9PersonsToPositions> personsToPositionsList) {
        String jobName = y9Job.getName();
        String personNames;

        if (personsToPositionsList.isEmpty()) {
            personNames = "空缺";
        } else {
            List<Y9Person> personList = new ArrayList<>();
            for (Y9PersonsToPositions y9PersonsToPositions : personsToPositionsList) {
                Y9Person person = y9PersonManager.getById(y9PersonsToPositions.getPersonId());
                personList.add(person);
            }
            personNames = personList.stream()
                .sorted((Comparator.comparing(Y9Person::getOrderedPath)))
                .map(Y9OrgBase::getName)
                .collect(Collectors.joining("，"));
        }

        return parseSpringEl(y9SettingService.getTenantSetting().getPositionNameTemplate(), jobName, personNames);
    }

    /**
     * springEL 支持，用于更灵活的岗位名称 <br>
     * 例如：positionNameTemplate 为 "#jobName.equals('无') ? #personNames : #jobName + '（' + #personNames + '）'" <br>
     *
     * @param positionNameTemplate 有 springEL 表达式的职位名称模板
     * @param jobName 职位名
     * @param personNames 人员名称
     * @return {@code String } 计算后的岗位名称
     */
    private String parseSpringEl(String positionNameTemplate, String jobName, String personNames) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(positionNameTemplate);
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("jobName", jobName);
        context.setVariable("personNames", personNames);
        return expression.getValue(context, String.class);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Position.id")
    public void delete(Y9Position y9Position) {
        y9PositionRepository.delete(y9Position);

        // 发布事件，程序内部监听处理相关业务
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Position));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Position> findById(String id) {
        return y9PositionRepository.findById(id);
    }

    @Override
    public Optional<Y9Position> findByIdNotCache(String id) {
        return y9PositionRepository.findById(id);
    }

    @Override
    public Y9Position getByIdNotCache(String id) {
        return y9PositionRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.POSITION_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Position getById(String id) {
        return y9PositionRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.POSITION_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#position.id")
    public Y9Position save(Y9Position position) {
        return y9PositionRepository.save(position);
    }

    @Transactional(readOnly = false)
    @Override
    public Y9Position insert(Y9Position position) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(position.getParentId());
        Y9Job y9Job = y9JobManager.getById(position.getJobId());

        if (StringUtils.isEmpty(position.getId())) {
            position.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            position.setDisabled(false);
        }
        position.setTabIndex((null == position.getTabIndex() || DefaultConsts.TAB_INDEX.equals(position.getTabIndex()))
            ? compositeOrgBaseManager.getNextSubTabIndex(parent.getId()) : position.getTabIndex());
        position.setTenantId(Y9LoginUserHolder.getTenantId());
        position.setJobName(y9Job.getName());
        position.setName(this.buildName(y9Job, Collections.emptyList()));
        position.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.POSITION, position.getName(), parent.getDn()));
        position.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(position));
        position.setParentId(parent.getId());
        position.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), position.getId()));

        final Y9Position savedPosition = save(position);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedPosition));

        return savedPosition;
    }

    @Transactional(readOnly = false)
    @CacheEvict(key = "#position.id")
    @Override
    public Y9Position update(Y9Position position) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(position.getParentId());
        Y9Position currentPosition = this.getById(position.getId());
        Y9Position originY9Position = Y9ModelConvertUtil.convert(currentPosition, Y9Position.class);

        Y9BeanUtil.copyProperties(position, currentPosition, "tenantId");
        currentPosition.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(currentPosition));
        currentPosition.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), currentPosition.getId()));
        currentPosition.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.POSITION, currentPosition.getName(), parent.getDn()));

        final Y9Position savedY9Position = save(currentPosition);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originY9Position, savedY9Position));

        return savedY9Position;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Position updateTabIndex(String id, int tabIndex) {
        Y9Position position = this.getById(id);

        Y9Position updatedPosition = Y9ModelConvertUtil.convert(position, Y9Position.class);
        updatedPosition.setTabIndex(tabIndex);
        updatedPosition.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(position));
        return this.update(updatedPosition);
    }
}
