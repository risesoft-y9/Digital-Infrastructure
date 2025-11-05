package net.risesoft.service.relation.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.relation.Y9CustomGroupMember;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9PersonManager;
import net.risesoft.manager.relation.Y9CustomGroupMembersManager;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.query.platform.CustomGroupMemberQuery;
import net.risesoft.repository.relation.Y9CustomGroupMembersRepository;
import net.risesoft.service.relation.Y9CustomGroupMembersService;
import net.risesoft.specification.Y9CustomGroupMemberSpecification;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9CustomGroupMembersServiceImpl implements Y9CustomGroupMembersService {

    private final Y9CustomGroupMembersRepository customGroupMembersRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9CustomGroupMembersManager y9CustomGroupMembersManager;
    private final Y9PersonManager y9PersonManager;

    @Override
    @Transactional
    public void delete(List<String> memberIdList) {
        for (String id : memberIdList) {
            delete(id);
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        customGroupMembersRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Y9Person> listAllPersonsByGroupId(String groupId) {
        List<Y9Person> orgPersonList = new ArrayList<>();
        List<Y9CustomGroupMember> orgCustomGroupMemberList =
            customGroupMembersRepository.findByGroupIdOrderByTabIndexAsc(groupId);
        for (Y9CustomGroupMember y9CustomGroupMember : orgCustomGroupMemberList) {
            OrgTypeEnum orgType = y9CustomGroupMember.getMemberType();
            switch (orgType) {
                case ORGANIZATION:
                    orgPersonList.addAll(
                        compositeOrgBaseManager.listAllDescendantPersons(y9CustomGroupMember.getMemberId(), false));
                    break;
                case DEPARTMENT:
                    orgPersonList.addAll(
                        compositeOrgBaseManager.listAllDescendantPersons(y9CustomGroupMember.getMemberId(), false));
                    break;
                case POSITION:
                    orgPersonList
                        .addAll(y9PersonManager.listByPositionId(y9CustomGroupMember.getMemberId(), Boolean.FALSE));
                    break;
                case GROUP:
                    orgPersonList
                        .addAll(y9PersonManager.listByGroupId(y9CustomGroupMember.getMemberId(), Boolean.FALSE));
                    break;
                case PERSON:
                    orgPersonList.add(y9PersonManager.getByIdFromCache(y9CustomGroupMember.getMemberId()));
                    break;
                default:
            }
        }
        return orgPersonList;
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
    @Transactional
    public void save(List<String> orgUnitList, String groupId) {
        y9CustomGroupMembersManager.save(orgUnitList, groupId);
    }

    @Override
    @Transactional
    public Y9CustomGroupMember save(Y9CustomGroupMember member) {
        return customGroupMembersRepository.save(member);
    }

    @Override
    @Transactional
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
    @Transactional
    public void share(String sourceGroupId, String targetGroupId) {
        y9CustomGroupMembersManager.share(sourceGroupId, targetGroupId);
    }

    @Override
    public List<Y9CustomGroupMember> list(CustomGroupMemberQuery customGroupMemberQuery) {
        return customGroupMembersRepository.findAll(new Y9CustomGroupMemberSpecification(customGroupMemberQuery));
    }

    @Override
    public Page<Y9CustomGroupMember> page(CustomGroupMemberQuery customGroupMemberQuery, Y9PageQuery pageQuery) {
        Y9CustomGroupMemberSpecification specification = new Y9CustomGroupMemberSpecification(customGroupMemberQuery);
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.ASC, "tabIndex"));
        return customGroupMembersRepository.findAll(specification, pageable);
    }

}
