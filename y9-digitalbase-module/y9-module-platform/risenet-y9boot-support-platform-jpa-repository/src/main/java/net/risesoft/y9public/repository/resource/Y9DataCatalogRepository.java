package net.risesoft.y9public.repository.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.resource.Y9DataCatalog;

/**
 * @author shidaobang
 * @date 2024/10/09
 * @since 9.6.8
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
// @JaversSpringDataAuditable
public interface Y9DataCatalogRepository extends JpaRepository<Y9DataCatalog, String> {

    List<Y9DataCatalog> findByParentIdAndTreeTypeOrderByTabIndexAsc(String parentId, String treeType);

    Optional<Y9DataCatalog> findTopByParentIdOrderByTabIndexDesc(String parentId);

    Optional<Y9DataCatalog> findByParentIdAndName(String parentId, String name);

    List<Y9DataCatalog> findByNameContainingAndTreeTypeOrderByTabIndex(String name, String treeType);

    List<Y9DataCatalog> findByParentId(String parentId);

    List<Y9DataCatalog> findByParentIdIsNullAndTreeTypeAndEnabledOrderByTabIndex(String treeType, Boolean enabled);

    Optional<Y9DataCatalog> findByParentIdAndOrgUnitId(String parentId, String id);

    List<Y9DataCatalog> findByOrgUnitId(String orgUnitId);

    List<Y9DataCatalog> findByParentIdIsNullAndTreeTypeOrderByTabIndex(String treeType);

    List<Y9DataCatalog> findByParentIdAndTreeTypeAndEnabledOrderByTabIndexAsc(String parentId, String treeType,
        Boolean enabled);
}
