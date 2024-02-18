package net.risesoft.listener;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.Y9Person;
import net.risesoft.util.Y9OrgUtil;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9Job;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;

/**
 * 监听需要更新岗位名称的事件并执行相应操作
 *
 * @author shidaobang
 * @date 2023/01/03
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdatePositionNameListener {

    private final Y9JobService y9JobService;
    private final Y9PositionService y9PositionService;
    private final Y9PersonsToPositionsService y9PersonsToPositionsService;
    private final CompositeOrgBaseService compositeOrgBaseService;

    @EventListener
    @Transactional(readOnly = false)
    public void onY9PositionUpdated(Y9EntityUpdatedEvent<Y9Position> event) {
        Y9Position updatedY9Position = event.getUpdatedEntity();

        this.updatePositionName(updatedY9Position);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新岗位触发的更新岗位名称执行完成");
        }
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onY9PersonUpdated(Y9EntityUpdatedEvent<Y9Person> event) {
        Y9Person originY9Person = event.getOriginEntity();
        Y9Person updatedY9Person = event.getUpdatedEntity();

        if (Y9OrgUtil.isRenamed(originY9Person, updatedY9Person)) {
            List<String> positionIdList =
                y9PersonsToPositionsService.listPositionIdsByPersonId(updatedY9Person.getId());
            for (String positionId : positionIdList) {
                this.updatePositionName(positionId);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新人员触发的更新岗位名称执行完成");
        }
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onY9JobUpdated(Y9EntityUpdatedEvent<Y9Job> event) {
        Y9Job y9Job = event.getUpdatedEntity();

        this.updatePositionName(y9Job);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新岗位触发的更新岗位名称执行完成");
        }
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onY9PersonsToPositionsCreated(Y9EntityCreatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        this.updatePositionName(y9PersonsToPositions.getPositionId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建人员和岗位的映射触发的更新岗位名称执行完成");
        }
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onY9PersonsToPositionsDeleted(Y9EntityDeletedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        this.updatePositionName(y9PersonsToPositions.getPositionId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("岗位删人触发的更新岗位名称执行完成");
        }
    }

    @Transactional(readOnly = false)
    public void updatePositionName(Y9Position y9Position) {
        Y9Job y9Job = y9JobService.getById(y9Position.getJobId());

        List<Y9PersonsToPositions> personsToPositionsList =
            y9PersonsToPositionsService.listByPositionId(y9Position.getId());

        String name = y9PositionService.buildName(y9Job, personsToPositionsList);

        Optional<Y9OrgBase> parentOptional = compositeOrgBaseService.findOrgUnitAsParent(y9Position.getParentId());
        if (parentOptional.isPresent()) {
            Y9OrgBase parent = parentOptional.get();

            y9Position.setName(name);
            y9Position.setJobId(y9Job.getId());
            y9Position.setJobName(y9Job.getName());
            y9Position.setHeadCount(personsToPositionsList.size());
            y9Position.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + y9Position.getId());
            y9Position.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.POSITION) + y9Position.getName()
                + OrgLevelConsts.SEPARATOR + parent.getDn());

            y9PositionService.save(y9Position);
        }
    }

    @Transactional(readOnly = false)
    public void updatePositionName(String positionId) {
        Optional<Y9Position> y9PositionOptional = y9PositionService.findById(positionId);
        if (y9PositionOptional.isPresent()) {
            this.updatePositionName(y9PositionOptional.get());
        }
    }

    @Transactional(readOnly = false)
    public void updatePositionName(Y9Job y9Job) {
        List<Y9Position> y9PositionList = y9PositionService.findByJobId(y9Job.getId());
        for (Y9Position y9Position : y9PositionList) {
            this.updatePositionName(y9Position);
        }
    }
}
