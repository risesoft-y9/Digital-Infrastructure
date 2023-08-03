package net.risesoft.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9CustomGroup;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9CustomGroupRepository
    extends JpaRepository<Y9CustomGroup, String>, JpaSpecificationExecutor<Y9CustomGroup> {

    Y9CustomGroup findByCustomId(String customId);

    Page<Y9CustomGroup> findByPersonId(String personId, Pageable pageable);

    List<Y9CustomGroup> findByPersonIdOrderByTabIndexAsc(String personId);

    @Query("select max(t.tabIndex) from Y9CustomGroup t where t.personId=?1 order by t.tabIndex desc")
    Integer getMaxTabIndex(String personId);
}
