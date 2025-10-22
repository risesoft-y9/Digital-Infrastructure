package net.risesoft.repository.dictionary;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.dictionary.Y9OptionValue;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9OptionValueRepository extends JpaRepository<Y9OptionValue, String> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByType(String type);

    List<Y9OptionValue> findByType(String type);

    Optional<Y9OptionValue> findByTypeAndName(String type, String name);

    Optional<Y9OptionValue> findTopByType(String type);
}
