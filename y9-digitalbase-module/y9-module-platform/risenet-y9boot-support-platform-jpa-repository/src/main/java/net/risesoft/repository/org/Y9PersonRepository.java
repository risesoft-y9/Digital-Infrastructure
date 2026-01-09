package net.risesoft.repository.org;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.risesoft.entity.org.Y9Person;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9PersonRepository extends JpaRepository<Y9Person, String>, JpaSpecificationExecutor<Y9Person> {

    long countByDisabledAndGuidPathContaining(Boolean disabled, String guidPath);

    List<Y9Person> findByDisabled(Boolean disabled);

    Optional<Y9Person> findByDisabledFalseAndMobileAndOriginal(String mobile, Boolean original);

    Optional<Y9Person> findByLoginNameAndOriginalTrue(String loginName);

    Optional<Y9Person> findByCaidAndOriginalTrue(String caid);

    Optional<Y9Person> findByLoginNameAndParentId(String loginName, String parentId);

    List<Y9Person> findByNameContaining(String name);

    List<Y9Person> findByNameContainingAndDisabled(String name, Boolean disable);

    List<Y9Person> findByNameContainingAndDnContaining(String name, String dnName);

    List<Y9Person> findByNameContainingAndDnContainingAndDisabled(String name, String dnName, Boolean disabled);

    List<Y9Person> findByOriginalId(String originalId);

    List<Y9Person> findByOriginalIdAndDisabled(String originalId, Boolean disable);

    Optional<Y9Person> findByOriginalIdAndParentId(String originalId, String parentId);

    List<Y9Person> findByParentIdAndDisabledAndNameContainingOrderByTabIndex(String parentId, Boolean disabled,
        String name);

    List<Y9Person> findByParentIdAndDisabledOrderByTabIndex(String parentId, Boolean disabled);

    List<Y9Person> findByParentIdAndNameContainingOrderByTabIndex(String parentId, String name);

    List<Y9Person> findByParentIdOrderByTabIndex(String parentId);

    @Query("select id from Y9Person where guidPath like ?1% and disabled = false")
    List<String> findIdByGuidPathStartingWith(String guidPath);

    Optional<Y9Person> findTopByParentIdOrderByTabIndexDesc(String parentId);

    @Query("select o.id from Y9Person o where o.dn like %?1%")
    List<String> findIdByDnContaining(String name);

    List<Y9Person> findByCaid(String caid);
}
