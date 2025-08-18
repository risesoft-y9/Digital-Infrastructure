package net.risesoft.manager.relation.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Group;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.relation.Y9CustomGroupMember;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.relation.Y9CustomGroupMembersManager;
import net.risesoft.repository.relation.Y9CustomGroupMembersRepository;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 自定义用户组 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9CustomGroupMembersManagerImpl implements Y9CustomGroupMembersManager {

    private final Y9CustomGroupMembersRepository customGroupMembersRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;

    @Override
    @Transactional(readOnly = false)
    public void save(List<String> orgUnitList, String groupId) {
        for (String id : orgUnitList) {
            Y9OrgBase y9OrgBase = compositeOrgBaseManager.getOrgUnit(id);
            Optional<Y9CustomGroupMember> optionalY9CustomGroupMember =
                customGroupMembersRepository.findByGroupIdAndMemberId(groupId, id);
            if (optionalY9CustomGroupMember.isEmpty()) {
                Integer tabIndex = customGroupMembersRepository.getMaxTabIndex(groupId);
                Y9CustomGroupMember member = new Y9CustomGroupMember();
                member.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                member.setMemberId(id);
                member.setMemberName(y9OrgBase.getName());
                member.setGroupId(groupId);
                member.setTabIndex(tabIndex == null ? 1 : tabIndex + 1);
                member.setMemberType(y9OrgBase.getOrgType());
                OrgTypeEnum orgType = y9OrgBase.getOrgType();
                switch (orgType) {
                    case ORGANIZATION:
                        break;
                    case DEPARTMENT:
                        Y9Department department = (Y9Department)y9OrgBase;
                        member.setParentId(department.getParentId());
                        break;
                    case POSITION:
                        Y9Position position = (Y9Position)y9OrgBase;
                        member.setParentId(position.getParentId());
                        break;
                    case GROUP:
                        Y9Group group = (Y9Group)y9OrgBase;
                        member.setParentId(group.getParentId());
                        break;
                    case PERSON:
                        Y9Person person = (Y9Person)y9OrgBase;
                        member.setParentId(person.getParentId());
                        member.setSex(person.getSex().getValue());
                        break;
                    default:
                }
                customGroupMembersRepository.save(member);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void share(String sourceGroupId, String targetGroupId) {
        List<Y9CustomGroupMember> customGroupMemberList =
            customGroupMembersRepository.findByGroupIdOrderByTabIndexAsc(sourceGroupId);
        for (Y9CustomGroupMember customGroupMember : customGroupMemberList) {
            Y9CustomGroupMember cgm = new Y9CustomGroupMember();
            Y9BeanUtil.copyProperties(customGroupMember, cgm);
            cgm.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            cgm.setGroupId(targetGroupId);
            customGroupMembersRepository.save(cgm);
        }
    }
}
