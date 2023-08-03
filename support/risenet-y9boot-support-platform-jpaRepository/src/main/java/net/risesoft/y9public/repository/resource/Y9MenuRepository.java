package net.risesoft.y9public.repository.resource;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.resource.Y9Menu;

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
public interface Y9MenuRepository extends JpaRepository<Y9Menu, String> {

    List<Y9Menu> findByNameContainingOrderByTabIndex(String name);

    Y9Menu findByParentIdAndCustomId(String parentId, String customId);

    List<Y9Menu> findByParentIdOrderByTabIndex(String parentId);

    Y9Menu findByUrl(String url);

}
