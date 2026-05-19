package net.risesoft.repository.permission.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.permission.cache.Y9PersonalApp;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9PersonalAppRepository extends JpaRepository<Y9PersonalApp, String> {

    Page<Y9PersonalApp> findByAppId(String appId, Pageable pageable);

    Page<Y9PersonalApp> findByOrgUnitId(String orgUnitId, Pageable pageable);

    Y9PersonalApp findByOrgUnitIdAndAppId(String orgUnitId, String appId);

    List<Y9PersonalApp> findByOrgUnitIdAndStar(String orgUnitId, Boolean star);

    Page<Y9PersonalApp> findByOrgUnitIdAndCategoryId(String orgUnitId, String categoryId, Pageable pageable);

    List<Y9PersonalApp> findByOrgUnitIdAndCategoryIdOrderByTabIndex(String orgUnitId, String categoryId);

    List<Y9PersonalApp> findByOrgUnitIdAndTabIndexBetweenOrderByTabIndex(String orgUnitId, Integer to, int i);

    List<Y9PersonalApp> findByOrgUnitIdOrderByTabIndex(String orgUnitId);

    Y9PersonalApp findTopByOrgUnitIdAndTabIndex(String orgUnitId, Integer tabIndex);

    Optional<Y9PersonalApp> findTopByOrgUnitIdOrderByTabIndexDesc(String orgUnitId);

}
