package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.y9public.entity.Y9ApiAccessControl;

/**
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository(value = "apiAccessControlRepository")
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9ApiAccessControlRepository extends JpaRepository<Y9ApiAccessControl, String> {

    List<Y9ApiAccessControl> findByTypeAndEnabledTrueOrderByCreateTime(ApiAccessControlType type);

}
