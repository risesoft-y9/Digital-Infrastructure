package net.risesoft.y9public.repository.role;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    List<Y9Role> findByCustomId(String customId);

    Y9Role findByCustomIdAndParentId(String customId, String parentId);

    Y9Role findByDnAndType(String dn, String type);

    Page<Y9Role> findByNameAndSystemCnNameAndType(String name, String systemCnName, String type, Pageable pageable);

    Page<Y9Role> findByNameAndSystemCnNameAndTypeAndParentIdNotIn(String name, String systemCnName, String type,
        List<String> ids, Pageable pageable);

    List<Y9Role> findByNameAndSystemNameAndPropertiesAndType(String name, String systemName, String properties,
        String type);

    List<Y9Role> findByNameAndSystemNameAndType(String name, String systemName, String type);

    Page<Y9Role> findByNameAndType(String name, String type, Pageable pageable);

    Page<Y9Role> findByNameAndTypeAndDnContaining(String roleName, String type, String dn, Pageable pageable);

    Page<Y9Role> findByNameAndTypeAndParentIdNotIn(String name, String type, List<String> ids, Pageable pageable);

    List<Y9Role> findByNameContainingOrderByTabIndexAsc(String name);

    List<Y9Role> findByParentIdAndCustomIdAndSystemNameAndType(String parentId, String customId, String systemName,
        String type);

    List<Y9Role> findByParentIdAndName(String parentId, String name);

    List<Y9Role> findByParentIdAndSystemNameInOrderByTabIndexAsc(String parenId, List<String> systemNames);

    List<Y9Role> findByParentIdAndType(String parentId, String type);

    List<Y9Role> findByParentIdIsNullOrderByTabIndexAsc();

    List<Y9Role> findByParentIdOrderByTabIndexAsc(String parentId);

    Page<Y9Role> findBySystemCnNameAndType(String systemCnName, String type, Pageable pageable);

    Page<Y9Role> findBySystemCnNameAndTypeAndDnContaining(String systemCnName, String type, String dn,
        Pageable pageable);

    Page<Y9Role> findBySystemCnNameAndTypeAndParentId(String systemCnName, String type, String parentId,
        Pageable pageable);

    Page<Y9Role> findBySystemCnNameAndTypeAndParentIdNotIn(String systemCnName, String type, List<String> ids,
        Pageable pageable);

    List<Y9Role> findBySystemNameAndNameContainingOrderByTabIndexAsc(String systemName, String name);

    Page<Y9Role> findByType(String type, Pageable pageable);

    Page<Y9Role> findByTypeAndDnContaining(String type, String dn, Pageable pageable);

    Page<Y9Role> findByTypeAndParentId(String type, String parentId, Pageable pageable);

    Page<Y9Role> findByTypeAndParentIdNotIn(String type, List<String> ids, Pageable pageable);

    Y9Role findTopByOrderByTabIndexDesc();
}
