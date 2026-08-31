package net.risesoft.repository.relation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.risesoft.entity.relation.Y9SystemVendor;

/**
 * 系统开发商和系统关联 Repository
 *
 * @author shidaobang
 * @date 2026/8/31
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
public interface Y9SystemVendorRepository extends JpaRepository<Y9SystemVendor, String> {

    void deleteByManagerId(String managerId);

    void deleteByManagerIdAndSystemId(String managerId, String systemId);

    void deleteBySystemId(String systemId);

    boolean existsByManagerIdAndSystemId(String managerId, String systemId);

    List<Y9SystemVendor> findByManagerId(String managerId);

}
