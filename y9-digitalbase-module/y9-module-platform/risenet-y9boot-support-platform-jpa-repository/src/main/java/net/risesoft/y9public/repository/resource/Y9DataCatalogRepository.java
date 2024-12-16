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
public interface Y9DataCatalogRepository extends JpaRepository<Y9DataCatalog, String> {

    List<Y9DataCatalog> findByTenantIdAndParentIdAndTreeTypeOrderByTabIndexAsc(String tenantId, String parentId,
        String treeType);

    Optional<Y9DataCatalog> findTopByTenantIdAndParentIdOrderByTabIndexDesc(String tenantId, String parentId);

    Optional<Y9DataCatalog> findByTenantIdAndParentIdAndName(String tenantId, String parentId, String name);

    List<Y9DataCatalog> findByTenantIdAndNameContainingAndTreeTypeOrderByTabIndex(String tenantId, String name,
        String treeType);

    List<Y9DataCatalog> findByTenantIdAndParentId(String tenantId, String parentId);

    List<Y9DataCatalog> findByTenantIdAndParentIdIsNullAndTreeTypeAndEnabledOrderByTabIndex(String tenantId,
        String treeType, Boolean enabled);

    Optional<Y9DataCatalog> findByTenantIdAndParentIdAndOrgUnitId(String tenantId, String parentId, String id);

    List<Y9DataCatalog> findByTenantIdAndOrgUnitId(String tenantId, String orgUnitId);

    List<Y9DataCatalog> findByTenantIdAndParentIdIsNullAndTreeTypeOrderByTabIndex(String tenantId, String treeType);

    List<Y9DataCatalog> findByTenantIdAndParentIdAndTreeTypeAndEnabledOrderByTabIndexAsc(String tenantId,
        String parentId, String treeType, Boolean enabled);

    List<Y9DataCatalog> findByParentIdOrderByTabIndex(String parentId);

    List<Y9DataCatalog> findByParentIdIsNull();

}
