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
import net.risesoft.entity.Y9Job;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9JobManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.repository.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9Assert;
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
            personNames = personList.stream().sorted((Comparator.comparing(Y9Person::getOrderedPath)))
                .map(Y9OrgBase::getName).collect(Collectors.joining("，"));
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

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#position.id")
    public Y9Position saveOrUpdate(Y9Position position) {
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(position.getParentId());
        Y9Job y9Job = y9JobManager.getById(position.getJobId());

        if (StringUtils.isNotEmpty(position.getId())) {
            Optional<Y9Position> updatedY9PositionOptional = this.findByIdNotCache(position.getId());
            if (updatedY9PositionOptional.isPresent()) {
                Y9Position updatedY9Position = updatedY9PositionOptional.get();
                // 修改的岗位容量不能小于当前岗位人数
                checkHeadCountAvailability(position);

                Y9Position originY9Position = new Y9Position();
                Y9BeanUtil.copyProperties(updatedY9Position, originY9Position);
                Y9BeanUtil.copyProperties(position, updatedY9Position);

                updatedY9Position.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(updatedY9Position));
                updatedY9Position.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), updatedY9Position.getId()));
                updatedY9Position
                    .setDn(Y9OrgUtil.buildDn(OrgTypeEnum.POSITION, updatedY9Position.getName(), parent.getDn()));

                final Y9Position savedY9Position = save(updatedY9Position);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originY9Position, savedY9Position));

                return savedY9Position;
            }
        }
        if (StringUtils.isEmpty(position.getId())) {
            position.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            position.setDisabled(false);
        }
        position.setTabIndex(null == position.getTabIndex() ? compositeOrgBaseManager.getNextSubTabIndex(parent.getId())
            : position.getTabIndex());
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

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Position saveProperties(String id, String properties) {
        final Y9Position position = this.getById(id);

        Y9Position updatedPosition = Y9ModelConvertUtil.convert(position, Y9Position.class);
        updatedPosition.setProperties(properties);
        return this.saveOrUpdate(updatedPosition);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#id")
    public Y9Position updateTabIndex(String id, int tabIndex) {
        Y9Position position = this.getById(id);

        Y9Position updatedPosition = Y9ModelConvertUtil.convert(position, Y9Position.class);
        updatedPosition.setTabIndex(tabIndex);
        updatedPosition.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(position));
        return this.saveOrUpdate(updatedPosition);
    }

    private void checkHeadCountAvailability(Y9Position position) {
        Integer personCount = y9PersonsToPositionsRepository.countByPositionId(position.getId());
        Y9Assert.lessThanOrEqualTo(personCount, position.getCapacity(), OrgUnitErrorCodeEnum.POSITION_IS_FULL,
            position.getName());
    }
}
