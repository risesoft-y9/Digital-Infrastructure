package net.risesoft.y9public.repository.role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.y9public.entity.role.Y9Role;

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
public interface Y9RoleRepository extends JpaRepository<Y9Role, String>, JpaSpecificationExecutor<Y9Role> {

    List<Y9Role> findByAppIdAndParentId(String appId, String parentId);

    List<Y9Role> findByAppIdAndParentIdIsNull(String appId);

    Optional<Y9Role> findByCustomIdAndParentId(String customId, String parentId);

    List<Y9Role> findByNameAndSystemNameAndPropertiesAndType(String name, String systemName, String properties,
        RoleTypeEnum type);

    List<Y9Role> findByNameAndSystemNameAndType(String name, String systemName, RoleTypeEnum type);

    List<Y9Role> findByNameContainingOrderByTabIndexAsc(String name);

    List<Y9Role> findByParentIdAndName(String parentId, String name);

    List<Y9Role> findByParentIdAndNameContainingOrderByTabIndexAsc(String parentId, String name);

    List<Y9Role> findByParentIdIsNullOrderByTabIndexAsc();

    List<Y9Role> findByParentIdOrderByTabIndexAsc(String parentId);

    Optional<Y9Role> findTopByOrderByTabIndexDesc();
}
