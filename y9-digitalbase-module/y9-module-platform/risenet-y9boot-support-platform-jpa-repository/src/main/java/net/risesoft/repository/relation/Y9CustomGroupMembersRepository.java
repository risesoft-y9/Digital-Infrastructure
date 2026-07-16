package net.risesoft.repository.relation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.relation.Y9CustomGroupMember;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9CustomGroupMembersRepository
    extends JpaRepository<Y9CustomGroupMember, String>, JpaSpecificationExecutor<Y9CustomGroupMember> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByGroupId(String groupId);

    Optional<Y9CustomGroupMember> findByGroupIdAndMemberId(String groupId, String memberId);

    List<Y9CustomGroupMember> findByGroupIdOrderByTabIndexAsc(String groupId);

    @Query("select max(t.tabIndex) from Y9CustomGroupMember t where t.groupId=?1 order by t.tabIndex desc")
    Integer getMaxTabIndex(String groupId);

}
