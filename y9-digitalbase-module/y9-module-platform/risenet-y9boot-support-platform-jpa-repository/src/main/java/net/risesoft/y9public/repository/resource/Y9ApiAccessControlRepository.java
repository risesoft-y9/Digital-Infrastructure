package net.risesoft.y9public.repository.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.y9public.entity.resource.Y9ApiAccessControl;

/**
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9ApiAccessControlRepository extends JpaRepository<Y9ApiAccessControl, String> {

    List<Y9ApiAccessControl> findByTypeOrderByCreateTime(ApiAccessControlType type);

    List<Y9ApiAccessControl> findByTypeAndEnabledTrueOrderByCreateTime(ApiAccessControlType type);

    Optional<Y9ApiAccessControl> findTopByTypeOrderByCreateTime(ApiAccessControlType type);
}
