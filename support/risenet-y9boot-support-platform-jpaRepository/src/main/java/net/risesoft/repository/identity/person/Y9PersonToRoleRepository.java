package net.risesoft.repository.identity.person;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.identity.person.Y9PersonToRole;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9PersonToRoleRepository extends JpaRepository<Y9PersonToRole, Integer> {

    long countByPersonId(String personId);

    int countByPersonIdAndRoleCustomId(String personId, String customId);

    int countByPersonIdAndRoleId(String personId, String roleId);

    long countByPersonIdAndSystemName(String personId, String systemName);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByPersonId(String personId);

    @Modifying
    @Transactional(readOnly = false)
    void deleteByRoleId(String roleId);

    List<Y9PersonToRole> findByPersonId(String personId);

    Page<Y9PersonToRole> findByPersonId(String personId, Pageable pageable);

    Optional<Y9PersonToRole> findByPersonIdAndRoleId(String personId, String roleId);

    List<Y9PersonToRole> findByRoleId(String roleId);

    @Query("select p.roleId from Y9PersonToRole p where p.personId = ?1")
    List<String> findRoleIdByPersonId(String personId);
}
