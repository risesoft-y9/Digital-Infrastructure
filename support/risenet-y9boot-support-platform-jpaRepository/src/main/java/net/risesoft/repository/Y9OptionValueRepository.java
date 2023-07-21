package net.risesoft.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9OptionValue;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9OptionValueRepository extends JpaRepository<Y9OptionValue, String> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByType(String type);

    List<Y9OptionValue> findByType(String type);

    Y9OptionValue findByTypeAndName(String type, String name);

    Page<Y9OptionValue> findPageByType(String type, Pageable pageable);

    Page<Y9OptionValue> findPageByTypeAndNameContaining(String type, String name, Pageable pageable);

    Y9OptionValue findTopByType(String type);
}
