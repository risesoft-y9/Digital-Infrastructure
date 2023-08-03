package net.risesoft.service.org.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9CustomGroup;
import net.risesoft.entity.Y9Person;
import net.risesoft.exception.CustomGroupErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.relation.Y9CustomGroupMembersManager;
import net.risesoft.repository.Y9CustomGroupRepository;
import net.risesoft.repository.Y9PersonRepository;
import net.risesoft.repository.relation.Y9CustomGroupMembersRepository;
import net.risesoft.service.org.Y9CustomGroupService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9CustomGroupServiceImpl implements Y9CustomGroupService {

    private final Y9CustomGroupRepository customGroupRepository;
    private final Y9CustomGroupMembersRepository customGroupMembersRepository;
    private final Y9PersonRepository y9PersonRepository;

    private final Y9CustomGroupMembersManager y9CustomGroupMembersManager;

    @Override
    @Transactional(readOnly = false)
    public void delete(List<String> idList) {
        for (String id : idList) {
            customGroupRepository.deleteById(id);
            customGroupMembersRepository.deleteByGroupId(id);
        }
    }

    @Override
    public Y9CustomGroup findByCustomId(String customId) {
        return customGroupRepository.findByCustomId(customId);
    }

    @Override
    public Y9CustomGroup findById(String id) {
        return customGroupRepository.findById(id).orElse(null);
    }

    @Override
    public Y9CustomGroup getById(String id) {
        return customGroupRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(CustomGroupErrorCodeEnum.CUSTOM_GROUP_NOT_FOUND, id));
    }

    @Override
    public List<Y9CustomGroup> listByPersonId(String personId) {
        return StringUtils.isNotEmpty(personId) ? customGroupRepository.findByPersonIdOrderByTabIndexAsc(personId)
            : null;
    }

    @Override
    public Page<Y9CustomGroup> pageByPersonId(int page, int rows, String personId) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, Sort.by(Sort.Direction.ASC, "tabIndex"));
        return customGroupRepository.findByPersonId(personId, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9CustomGroup save(Y9CustomGroup y9CustomGroup) {
        if (StringUtils.isBlank(y9CustomGroup.getId())) {
            y9CustomGroup.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        y9CustomGroup.setTenantId(Y9LoginUserHolder.getTenantId());
        return customGroupRepository.save(y9CustomGroup);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean saveCustomGroupOrder(List<String> sortIdList) {
        for (int i = 0; i < sortIdList.size(); i++) {
            Y9CustomGroup customGroup = this.getById(sortIdList.get(i));
            customGroup.setTabIndex(i);
            this.save(customGroup);
        }
        return true;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9CustomGroup saveOrUpdate(String personId, List<String> personIdList, String groupId, String groupName) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Y9CustomGroup group = new Y9CustomGroup();
        if (StringUtils.isBlank(groupId)) {
            String guid = Y9IdGenerator.genId(IdType.SNOWFLAKE);
            group.setId(guid);
            group.setTenantId(tenantId);
            group.setGroupName(groupName);
            Y9Person person = y9PersonRepository.findById(personId).orElse(null);
            if (null != person) {
                Integer tabIndex = customGroupRepository.getMaxTabIndex(person.getId());
                group.setPersonName(person.getName());
                group.setTabIndex(tabIndex == null ? 1 : tabIndex + 1);
                group.setPersonId(person.getId());
            }
            group = this.save(group);
        } else {
            group = this.getById(groupId);
            group.setGroupName(groupName);
            group = this.save(group);
        }
        y9CustomGroupMembersManager.save(personIdList, group.getId());
        return group;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean share(List<String> personIds, List<String> groupIds) {
        for (String personId : personIds) {
            for (String groupId : groupIds) {
                Y9CustomGroup y9CustomGroup = this.share(personId, groupId);
                y9CustomGroupMembersManager.share(groupId, y9CustomGroup.getId());
            }
        }
        return true;
    }

    @Transactional(readOnly = false)
    public Y9CustomGroup share(String personId, String groupId) {
        Y9CustomGroup customGroup = this.getById(groupId);
        Integer tabIndex = customGroupRepository.getMaxTabIndex(personId);
        Y9Person person = y9PersonRepository.findById(personId).orElse(null);
        Y9CustomGroup group = new Y9CustomGroup();
        String id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        group.setId(id);
        group.setGroupName(customGroup.getGroupName());
        group.setTabIndex(tabIndex == null ? 1 : tabIndex + 1);
        group.setTenantId(customGroup.getTenantId());
        group.setShareId(customGroup.getPersonId());
        group.setShareName(customGroup.getPersonName());
        if (null != person) {
            group.setPersonName(person.getName());
            group.setPersonId(person.getId());
        }
        return this.save(group);
    }

}
