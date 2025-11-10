package net.risesoft.listener;

import java.util.List;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9Job;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.platform.org.Job;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9OrgUtil;
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
    @Transactional
    public void onY9JobUpdated(Y9EntityUpdatedEvent<Y9Job> event) {
        Y9Job y9Job = event.getUpdatedEntity();

        this.updatePositionName(y9Job);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新岗位触发的更新岗位名称执行完成");
        }
    }

    @EventListener
    @Transactional
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
    @Transactional
    public void onY9PersonsToPositionsCreated(Y9EntityCreatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        this.updatePositionName(y9PersonsToPositions.getPositionId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建人员和岗位的映射触发的更新岗位名称执行完成");
        }
    }

    @EventListener
    @Transactional
    public void onY9PersonsToPositionsDeleted(Y9EntityDeletedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        this.updatePositionName(y9PersonsToPositions.getPositionId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("岗位删人触发的更新岗位名称执行完成");
        }
    }

    @EventListener
    @Transactional
    public void onY9PositionUpdated(Y9EntityUpdatedEvent<Y9Position> event) {
        Y9Position updatedY9Position = event.getUpdatedEntity();

        Position position = PlatformModelConvertUtil.y9PositionToPosition(updatedY9Position);
        this.updatePositionName(position);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新岗位触发的更新岗位名称执行完成");
        }
    }

    @Transactional
    public void updatePositionName(Position position) {
        Job job = y9JobService.getById(position.getJobId());

        List<Person> personList = y9PersonsToPositionsService.listPersonByPositionId(position.getId());

        String name = y9PositionService.buildName(job, personList);

        Optional<OrgUnit> parentOptional = compositeOrgBaseService.findOrgUnitAsParent(position.getParentId());
        if (parentOptional.isPresent()) {
            OrgUnit parent = parentOptional.get();

            position.setName(name);
            position.setJobId(job.getId());
            position.setJobName(job.getName());
            position.setHeadCount(personList.size());
            position.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), position.getId()));
            position.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.POSITION, position.getName(), parent.getDn()));

            y9PositionService.save(position);
        }
    }

    @Transactional
    public void updatePositionName(String positionId) {
        Optional<Position> positionOptional = y9PositionService.findById(positionId);
        if (positionOptional.isPresent()) {
            this.updatePositionName(positionOptional.get());
        }
    }

    @Transactional
    public void updatePositionName(Y9Job y9Job) {
        List<Position> positionList = y9PositionService.findByJobId(y9Job.getId());
        for (Position position : positionList) {
            this.updatePositionName(position);
        }
    }
}
