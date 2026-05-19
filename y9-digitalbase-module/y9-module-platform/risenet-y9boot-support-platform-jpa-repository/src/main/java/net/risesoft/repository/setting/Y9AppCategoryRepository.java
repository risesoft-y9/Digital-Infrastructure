package net.risesoft.repository.setting;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.setting.Y9AppCategory;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9AppCategoryRepository extends JpaRepository<Y9AppCategory, String> {

    Y9AppCategory findByAppId(String appId);

    List<Y9AppCategory> findByCategoryIdOrderByTabIndex(String categoryId);

    Page<Y9AppCategory> findPageByCategoryId(String categoryId, Pageable pageable);

    Optional<Y9AppCategory> findTopByCategoryIdOrderByTabIndexDesc(String categoryId);

}
