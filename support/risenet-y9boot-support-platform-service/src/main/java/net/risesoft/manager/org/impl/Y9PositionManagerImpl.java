package net.risesoft.manager.org.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.exception.PositionErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PositionManager;
import net.risesoft.model.Position;
import net.risesoft.repository.Y9PositionRepository;
import net.risesoft.repository.relation.Y9PersonsToPositionsRepository;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;
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

    private final CompositeOrgBaseManager compositeOrgBaseManager;

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Y9Position getById(String id) {
        return y9PositionRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(PositionErrorCodeEnum.POSITION_NOT_FOUND, id));
    }

    @Override
    @Cacheable(key = "#id", condition = "#id!=null", unless = "#result==null")
    public Optional<Y9Position> findById(String id) {
        return y9PositionRepository.findById(id);
    }

    private void checkHeadCountAvailability(Y9Position position) {
        Integer personCount = y9PersonsToPositionsRepository.countByPositionId(position.getId());
        Y9Assert.lessThanOrEqualTo(personCount, position.getCapacity(), PositionErrorCodeEnum.POSITION_IS_FULL,
            position.getName());
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#position.id")
    public Y9Position saveOrUpdate(Y9Position position, Y9OrgBase parent) {
        if (StringUtils.isNotEmpty(position.getId())) {
            Y9Position originalPosition = y9PositionRepository.findById(position.getId()).orElse(null);
            if (originalPosition != null) {
                // 修改的岗位容量不能小于当前岗位人数
                checkHeadCountAvailability(position);

                Y9Position updatedY9Position = new Y9Position();
                Y9BeanUtil.copyProperties(originalPosition, updatedY9Position);
                Y9BeanUtil.copyProperties(position, updatedY9Position);

                if (null != parent) {
                    updatedY9Position.setParentId(parent.getId());
                    updatedY9Position
                        .setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + updatedY9Position.getId());
                    updatedY9Position.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.POSITION)
                        + updatedY9Position.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());
                } else {
                    updatedY9Position.setGuidPath(updatedY9Position.getId());
                    updatedY9Position
                        .setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.POSITION) + updatedY9Position.getName());
                }

                updatedY9Position = save(updatedY9Position);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalPosition, updatedY9Position));

                Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(updatedY9Position, Position.class),
                    Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_POSITION, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新岗位信息", "更新" + position.getName());

                return updatedY9Position;
            } else {
                if (null == position.getTabIndex()) {
                    position.setTabIndex(Integer.MAX_VALUE);
                }
                position.setOrgType(OrgTypeEnum.POSITION.getEnName());
                if (null == position.getDutyLevel()) {
                    position.setDutyLevel(0);
                }

                if (null != parent) {
                    position.setParentId(parent.getId());
                    position.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + position.getId());
                    position.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.POSITION) + position.getName()
                        + OrgLevelConsts.SEPARATOR + parent.getDn());
                } else {
                    position.setGuidPath(position.getId());
                    position.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.POSITION) + position.getName());
                }

                Y9Position returnPosition = save(position);

                Y9Context.publishEvent(new Y9EntityCreatedEvent<>(returnPosition));

                Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(returnPosition, Position.class),
                    Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_POSITION, Y9LoginUserHolder.getTenantId());
                Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增岗位信息", "新增" + position.getName());

                return returnPosition;
            }
        }
        if (StringUtils.isEmpty(position.getId())) {
            position.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        position.setTabIndex(compositeOrgBaseManager.getMaxSubTabIndex(parent.getId(), OrgTypeEnum.POSITION));
        position.setOrgType(OrgTypeEnum.POSITION.getEnName());
        position.setDutyLevel(0);
        position.setDisabled(false);
        position.setParentId(parent.getId());
        position.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + position.getId());
        position.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.POSITION) + position.getName() + OrgLevelConsts.SEPARATOR
            + parent.getDn());
        Y9Position returnPosition = save(position);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(returnPosition));

        Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(returnPosition, Position.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_ADD_POSITION, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "新增岗位信息", "新增" + position.getName());

        return returnPosition;
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#position.id")
    public Y9Position save(Y9Position position) {
        StringBuilder sb = new StringBuilder();
        compositeOrgBaseManager.getOrderedPathRecursiveUp(sb, position);
        position.setOrderedPath(sb.toString());
        return y9PositionRepository.save(position);
    }

    @Override
    @Transactional(readOnly = false)
    @CacheEvict(key = "#y9Position.id")
    public void delete(Y9Position y9Position) {
        y9PositionRepository.delete(y9Position);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Position updateTabIndex(String id, int tabIndex) {
        Y9Position position = this.getById(id);
        position.setTabIndex(tabIndex);
        position = this.save(position);

        Y9MessageOrg msg = new Y9MessageOrg(Y9ModelConvertUtil.convert(position, Position.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_UPDATE_POSITION_TABINDEX, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "更新岗位排序号", position.getName() + "的排序号更新为" + tabIndex);

        return position;
    }
}
