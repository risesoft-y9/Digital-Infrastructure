package net.risesoft.repository.org;

import java.util.List;
import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.risesoft.entity.org.Y9Organization;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@JaversSpringDataAuditable
public interface Y9OrganizationRepository extends JpaRepository<Y9Organization, String> {

    List<Y9Organization> findByDisabledOrderByTabIndexAsc(Boolean disabled);

    List<Y9Organization> findByDn(String dn);

    List<Y9Organization> findByNameContainingAndDisabledOrderByTabIndexAsc(String name, Boolean disabled);

    List<Y9Organization> findByNameContainingOrderByTabIndexAsc(String name);

    List<Y9Organization> findByOrderByTabIndexAsc();

    List<Y9Organization> findByTenantIdOrderByTabIndexAsc(String tenantId);

    List<Y9Organization> findByVirtualAndDisabledOrderByTabIndexAsc(Boolean virtual, Boolean disabled);

    List<Y9Organization> findByVirtualOrderByTabIndexAsc(Boolean virtual);

    Optional<Y9Organization> findTopByOrderByTabIndexDesc();

    @Query("select o.id from Y9Organization o where o.dn like %?1%")
    List<String> findIdByDnContaining(String name);
}
