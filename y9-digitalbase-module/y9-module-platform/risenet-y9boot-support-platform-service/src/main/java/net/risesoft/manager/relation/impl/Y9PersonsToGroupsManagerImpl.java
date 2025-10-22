package net.risesoft.manager.relation.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.manager.relation.Y9PersonsToGroupsManager;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;

/**
 * 人员-用户组关联 Manager 实现类
 * 
 * @author shidaobang
 * @date 2023/09/18
 * @since 9.6.3
 */
@Service
@RequiredArgsConstructor
public class Y9PersonsToGroupsManagerImpl implements Y9PersonsToGroupsManager {

    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;

    @Override
    public void delete(Y9PersonsToGroups y9PersonsToGroups) {
        y9PersonsToGroupsRepository.delete(y9PersonsToGroups);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9PersonsToGroups));
    }

    @Override
    public void deleteByGroupId(String groupId) {
        List<Y9PersonsToGroups> y9PersonsToGroupsList = y9PersonsToGroupsRepository.findByGroupId(groupId);
        for (Y9PersonsToGroups y9PersonsToGroups : y9PersonsToGroupsList) {
            this.delete(y9PersonsToGroups);
        }
    }

    @Override
    public void deleteByPersonId(String personId) {
        y9PersonsToGroupsRepository.deleteByPersonId(personId);
    }
}
