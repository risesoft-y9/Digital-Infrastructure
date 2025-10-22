package net.risesoft.repository.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.risesoft.entity.dictionary.Y9OptionClass;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9OptionClassRepository extends JpaRepository<Y9OptionClass, String> {

    Y9OptionClass findByName(String name);
}
