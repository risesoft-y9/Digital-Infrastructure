package net.risesoft.manager.relation.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.relation.Y9PersonsToGroups;
import net.risesoft.manager.org.Y9GroupManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.relation.Y9PersonsToGroupsManager;
import net.risesoft.model.platform.PersonsGroups;
import net.risesoft.repository.relation.Y9PersonsToGroupsRepository;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9OrgEventConst;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.message.Y9MessageOrg;

/**
 * 人员-用户组关联 Manager 实现类
 * 
 * @author shidaobang
 * @date 2023/09/18
 * @since 9.6.3
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9PersonsToGroupsManagerImpl implements Y9PersonsToGroupsManager {

    private final Y9PersonManager y9PersonManager;
    private final Y9GroupManager y9GroupManager;

    private final Y9PersonsToGroupsRepository y9PersonsToGroupsRepository;

    @Override
    @Transactional(readOnly = false)
    public void deleteByGroupId(String groupId) {
        List<Y9PersonsToGroups> y9PersonsToGroupsList = y9PersonsToGroupsRepository.findByGroupId(groupId);
        for (Y9PersonsToGroups y9PersonsToGroups : y9PersonsToGroupsList) {
            this.delete(y9PersonsToGroups);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Y9PersonsToGroups y9PersonsToGroups) {
        y9PersonsToGroupsRepository.delete(y9PersonsToGroups);

        Y9Person person = y9PersonManager.getById(y9PersonsToGroups.getPersonId());
        Y9Group group = y9GroupManager.getById(y9PersonsToGroups.getGroupId());
        Y9MessageOrg msg = new Y9MessageOrg(ModelConvertUtil.convert(y9PersonsToGroups, PersonsGroups.class),
            Y9OrgEventConst.RISEORGEVENT_TYPE_GROUP_REMOVEPERSON, Y9LoginUserHolder.getTenantId());
        Y9PublishServiceUtil.persistAndPublishMessageOrg(msg, "移除用户组人员",
            group.getName() + "移除用户组成员" + person.getName());

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9PersonsToGroups));
    }
}
