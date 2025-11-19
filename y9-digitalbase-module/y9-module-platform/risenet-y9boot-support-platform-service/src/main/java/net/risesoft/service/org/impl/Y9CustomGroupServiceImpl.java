package net.risesoft.service.org.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9CustomGroup;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.relation.Y9CustomGroupMembersManager;
import net.risesoft.model.platform.org.CustomGroup;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.org.Y9CustomGroupRepository;
import net.risesoft.repository.relation.Y9CustomGroupMembersRepository;
import net.risesoft.service.org.Y9CustomGroupService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9CustomGroupServiceImpl implements Y9CustomGroupService {

    private final Y9CustomGroupRepository customGroupRepository;
    private final Y9CustomGroupMembersRepository customGroupMembersRepository;

    private final Y9CustomGroupMembersManager y9CustomGroupMembersManager;

    private static CustomGroup entityToModel(Y9CustomGroup y9CustomGroup) {
        return PlatformModelConvertUtil.convert(y9CustomGroup, CustomGroup.class);
    }

    private static List<CustomGroup> entityToModel(List<Y9CustomGroup> y9CustomGroupList) {
        return PlatformModelConvertUtil.convert(y9CustomGroupList, CustomGroup.class);
    }

    @Override
    @Transactional
    public void delete(List<String> idList) {
        for (String id : idList) {
            customGroupRepository.deleteById(id);
            customGroupMembersRepository.deleteByGroupId(id);
        }
    }

    @Override
    public Optional<CustomGroup> findByCustomId(String customId) {
        return customGroupRepository.findByCustomId(customId).map(Y9CustomGroupServiceImpl::entityToModel);
    }

    @Override
    public Optional<CustomGroup> findById(String id) {
        return customGroupRepository.findById(id).map(Y9CustomGroupServiceImpl::entityToModel);
    }

    @Override
    public CustomGroup getById(String id) {
        return entityToModel(get(id));
    }

    private Y9CustomGroup get(String id) {
        return customGroupRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.CUSTOM_GROUP_NOT_FOUND, id));
    }

    @Override
    public List<CustomGroup> listByPersonId(String personId) {
        return entityToModel(customGroupRepository.findByPersonIdOrderByTabIndexAsc(personId));
    }

    @Override
    public Y9Page<CustomGroup> pageByPersonId(String personId, Y9PageQuery pageQuery) {
        PageRequest pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.ASC, "tabIndex"));
        Page<Y9CustomGroup> y9CustomGroupPage = customGroupRepository.findByPersonId(personId, pageable);
        return Y9Page.success(pageQuery.getPage(), y9CustomGroupPage.getTotalPages(),
            y9CustomGroupPage.getTotalElements(), entityToModel(y9CustomGroupPage.getContent()));
    }

    @Override
    @Transactional
    public CustomGroup save(CustomGroup customGroup) {
        Y9CustomGroup y9CustomGroup = PlatformModelConvertUtil.convert(customGroup, Y9CustomGroup.class);

        if (StringUtils.isBlank(y9CustomGroup.getId())) {
            y9CustomGroup.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        y9CustomGroup.setTenantId(Y9LoginUserHolder.getTenantId());
        return entityToModel(customGroupRepository.save(y9CustomGroup));
    }

    @Override
    @Transactional
    public boolean saveCustomGroupOrder(List<String> sortIdList) {
        for (int i = 0; i < sortIdList.size(); i++) {
            CustomGroup customGroup = this.getById(sortIdList.get(i));
            customGroup.setTabIndex(i);
            this.save(customGroup);
        }
        return true;
    }

    @Override
    @Transactional
    public CustomGroup saveOrUpdate(String personId, List<String> personIdList, String groupId, String groupName) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        CustomGroup group = new CustomGroup();
        if (StringUtils.isBlank(groupId)) {
            String guid = Y9IdGenerator.genId(IdType.SNOWFLAKE);
            group.setId(guid);
            group.setTenantId(tenantId);
            group.setGroupName(groupName);
            group.setTabIndex(getNextTabIndex(personId));
            group.setPersonId(personId);
            group = this.save(group);
        } else {
            group = this.getById(groupId);
            group.setGroupName(groupName);
            group = this.save(group);
        }
        y9CustomGroupMembersManager.save(personIdList, group.getId());
        return group;
    }

    private Integer getNextTabIndex(String personId) {
        return customGroupRepository.getMaxTabIndex(personId).map(index -> index + 1).orElse(1);
    }

    @Override
    @Transactional
    public boolean share(List<String> personIds, List<String> groupIds) {
        for (String personId : personIds) {
            for (String groupId : groupIds) {
                CustomGroup y9CustomGroup = this.share(personId, groupId);
                y9CustomGroupMembersManager.share(groupId, y9CustomGroup.getId());
            }
        }
        return true;
    }

    @Transactional
    public CustomGroup share(String personId, String groupId) {
        Y9CustomGroup customGroup = get(groupId);
        CustomGroup group = new CustomGroup();
        String id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        group.setId(id);
        group.setGroupName(customGroup.getGroupName());
        group.setTabIndex(getNextTabIndex(personId));
        group.setTenantId(customGroup.getTenantId());
        group.setShareId(customGroup.getPersonId());
        group.setPersonId(personId);
        return this.save(group);
    }

}
