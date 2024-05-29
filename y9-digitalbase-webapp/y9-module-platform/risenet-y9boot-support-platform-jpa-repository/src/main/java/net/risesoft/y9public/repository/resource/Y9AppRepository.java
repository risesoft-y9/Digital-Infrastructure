package net.risesoft.y9public.repository.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.resource.Y9App;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
// @JaversSpringDataAuditable
public interface Y9AppRepository extends JpaRepository<Y9App, String>, JpaSpecificationExecutor<Y9App> {

    long countBySystemId(String systemId);

    List<Y9App> findByAutoInitAndCheckedOrderByCreateTime(Boolean autoInit, Boolean checked);

    List<Y9App> findByCheckedOrderByCreateTime(Boolean checked);

    List<Y9App> findByCustomId(String customId);

    List<Y9App> findByEnabledOrderByTabIndex(Boolean enabled);

    List<Y9App> findByName(String appName);

    List<Y9App> findByNameContainingOrderByTabIndex(String name);

    List<Y9App> findBySystemId(String systemId);

    Optional<Y9App> findBySystemIdAndCustomId(String systemId, String customId);

    List<Y9App> findBySystemIdAndName(String systemId, String appName);

    List<Y9App> findBySystemIdAndUrl(String systemId, String url);

    List<Y9App> findBySystemIdOrderByTabIndex(String systemId);

    List<Y9App> findByUrlContaining(String url);

    Optional<Y9App> findTopByOrderByTabIndexDesc();

}
