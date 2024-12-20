package net.risesoft.y9public.repository.resource;

import java.util.List;
import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.resource.Y9Operation;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@JaversSpringDataAuditable
public interface Y9OperationRepository extends JpaRepository<Y9Operation, String> {

    List<Y9Operation> findByAppId(String appId);

    List<Y9Operation> findByCustomId(String customId);

    List<Y9Operation> findByNameContainingOrderByTabIndex(String name);

    Optional<Y9Operation> findByParentIdAndCustomId(String parentId, String customId);

    List<Y9Operation> findByParentIdOrderByTabIndex(String parentId);

    Optional<Y9Operation> findTopByParentIdOrderByTabIndexDesc(String parentId);

}
