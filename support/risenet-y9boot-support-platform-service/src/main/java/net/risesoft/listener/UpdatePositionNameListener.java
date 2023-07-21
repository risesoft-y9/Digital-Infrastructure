package net.risesoft.listener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9Job;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9PersonsToPositions;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.manager.org.Y9OrgBaseManager;
import net.risesoft.service.org.Y9JobService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;
import net.risesoft.y9.configuration.Y9Properties;
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
    
    private final Y9Properties y9Properties;
    private final Y9JobService y9JobService;
    private final Y9PositionService y9PositionService;
    private final Y9PersonService y9PersonService;
    private final Y9PersonsToPositionsService y9PersonsToPositionsService;
    private final Y9OrgBaseManager y9OrgBaseManager;

    @TransactionalEventListener
    @Async
    public void onY9PositionUpdated(Y9EntityUpdatedEvent<Y9Position> event) {
        Y9Position updatedY9Position = event.getUpdatedEntity();

        this.updatePositionName(updatedY9Position);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新岗位触发的更新岗位名称执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9JobUpdated(Y9EntityUpdatedEvent<Y9Job> event) {
        Y9Job y9Job = event.getUpdatedEntity();

        this.updatePositionName(y9Job);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("更新岗位触发的更新岗位名称执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToPositionsCreated(Y9EntityCreatedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        this.updatePositionName(y9PersonsToPositions.getPositionId());
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("新建人员和岗位的映射触发的重新计算权限缓存执行完成");
        }
    }

    @TransactionalEventListener
    @Async
    public void onY9PersonsToPositionsDeleted(Y9EntityDeletedEvent<Y9PersonsToPositions> event) {
        Y9PersonsToPositions y9PersonsToPositions = event.getEntity();

        this.updatePositionName(y9PersonsToPositions.getPositionId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("岗位删人触发的更新岗位名称执行完成");
        }
    }

    public void updatePositionName(Y9Position y9Position) {
        Y9Job y9Job = y9JobService.getById(y9Position.getJobId());
        
        String name;
        List<Y9PersonsToPositions> personsToPositionsList = y9PersonsToPositionsService.listByPositionId(y9Position.getId());
        int headcount = personsToPositionsList.size();

        String pattern = y9Properties.getApp().getPlatform().getPositionNamePattern();

        if (headcount == 0) {
            name = MessageFormat.format(pattern, y9Job.getName(), "空缺");
        } else {
            List<Y9Person> personList = new ArrayList<>();
            for (Y9PersonsToPositions y9PersonsToPositions : personsToPositionsList) {
                Y9Person person = y9PersonService.getById(y9PersonsToPositions.getPersonId());
                personList.add(person);
            }
            String personNames = personList.stream().sorted((Comparator.comparing(Y9Person::getOrderedPath))).map(Y9OrgBase::getName).collect(Collectors.joining("，"));
            name = MessageFormat.format(pattern, y9Job.getName(), personNames);
        }

        Y9OrgBase parent = y9OrgBaseManager.getOrgBase(y9Position.getParentId());
        y9Position.setName(name);
        y9Position.setHeadCount(headcount);
        y9Position.setGuidPath(parent.getGuidPath() + OrgLevelConsts.SEPARATOR + y9Position.getId());
        y9Position.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.POSITION) + y9Position.getName() + OrgLevelConsts.SEPARATOR + parent.getDn());

        y9PositionService.save(y9Position);
    }

    public void updatePositionName(String positionId) {
        Y9Position y9Position = y9PositionService.findById(positionId);
        if (y9Position != null) {
            this.updatePositionName(y9Position);
        }
    }

    public void updatePositionName(Y9Job y9Job) {
        List<Y9Position> y9PositionList = y9PositionService.findByJobId(y9Job.getId());
        for (Y9Position y9Position : y9PositionList) {
            this.updatePositionName(y9Position);
        }
    }
}
