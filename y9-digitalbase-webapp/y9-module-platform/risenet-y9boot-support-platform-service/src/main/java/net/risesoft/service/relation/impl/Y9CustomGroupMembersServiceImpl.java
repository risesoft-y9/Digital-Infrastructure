package net.risesoft.service.relation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.relation.Y9CustomGroupMember;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.relation.Y9CustomGroupMembersManager;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.relation.Y9CustomGroupMembersRepository;
import net.risesoft.service.relation.Y9CustomGroupMembersService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9CustomGroupMembersServiceImpl implements Y9CustomGroupMembersService {

    private final Y9CustomGroupMembersRepository customGroupMembersRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9CustomGroupMembersManager y9CustomGroupMembersManager;
    private final Y9PersonManager y9PersonManager;

    @Override
    @Transactional(readOnly = false)
    public void delete(List<String> memberIdList) {
        for (String id : memberIdList) {
            delete(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        customGroupMembersRepository.deleteById(id);
    }

    @Override
    public Integer getMaxTabIndex(String groupId) {
        return customGroupMembersRepository.getMaxTabIndex(groupId);
    }

    @Override
    public List<Y9Person> listAllPersonsByGroupId(String groupId) {
        List<Y9Person> orgPersonList = new ArrayList<>();
        List<Y9CustomGroupMember> orgCustomGroupMemberList =
            customGroupMembersRepository.findByGroupIdOrderByTabIndexAsc(groupId);
        for (Y9CustomGroupMember y9CustomGroupMember : orgCustomGroupMemberList) {
            OrgTypeEnum orgType = y9CustomGroupMember.getMemberType();
            switch (orgType) {
                case ORGANIZATION:
                    orgPersonList.addAll(compositeOrgBaseManager
                        .listAllPersonsRecursionDownward(y9CustomGroupMember.getMemberId(), false));
                    break;
                case DEPARTMENT:
                    orgPersonList.addAll(compositeOrgBaseManager
                        .listAllPersonsRecursionDownward(y9CustomGroupMember.getMemberId(), false));
                    break;
                case POSITION:
                    orgPersonList.addAll(y9PersonManager.listByPositionId(y9CustomGroupMember.getMemberId()));
                    break;
                case GROUP:
                    orgPersonList.addAll(y9PersonManager.listByGroupId(y9CustomGroupMember.getMemberId()));
                    break;
                case PERSON:
                    orgPersonList.add(y9PersonManager.getById(y9CustomGroupMember.getMemberId()));
                    break;
                default:
            }
        }
        return orgPersonList;
    }

    @Override
    public List<Y9CustomGroupMember> listByGroupId(String groupId) {
        return StringUtils.isNotEmpty(groupId) ? customGroupMembersRepository.findByGroupIdOrderByTabIndexAsc(groupId)
            : null;
    }

    @Override
    public List<Y9CustomGroupMember> listByGroupIdAndMemberType(String groupId, OrgTypeEnum memberType) {
        return customGroupMembersRepository.findByGroupIdAndMemberTypeOrderByTabIndexAsc(groupId, memberType);
    }

    @Override
    public Page<Y9CustomGroupMember> pageByGroupId(String groupId, Y9PageQuery pageQuery) {
        Sort sort = Sort.by(Sort.Direction.ASC, "tabIndex");
        Pageable pageable = PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), sort);
        return customGroupMembersRepository.findByGroupId(groupId, pageable);
    }

    @Override
    public Page<Y9CustomGroupMember> pageByGroupIdAndMemberType(String groupId, OrgTypeEnum memberType,
        Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.ASC, "tabIndex"));
        return customGroupMembersRepository.findByGroupIdAndMemberType(groupId, memberType, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(List<String> orgUnitList, String groupId) {
        y9CustomGroupMembersManager.save(orgUnitList, groupId);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9CustomGroupMember save(Y9CustomGroupMember member) {
        return customGroupMembersRepository.save(member);
    }

    @Override
    @Transactional(readOnly = false)
    public boolean saveOrder(List<String> memberIdList) {
        for (int i = 0; i < memberIdList.size(); i++) {
            Y9CustomGroupMember member = customGroupMembersRepository.findById(memberIdList.get(i)).orElse(null);
            if (null != member) {
                member.setTabIndex(i);
                customGroupMembersRepository.save(member);
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = false)
    public void share(String sourceGroupId, String targetGroupId) {
        y9CustomGroupMembersManager.share(sourceGroupId, targetGroupId);
    }

}
