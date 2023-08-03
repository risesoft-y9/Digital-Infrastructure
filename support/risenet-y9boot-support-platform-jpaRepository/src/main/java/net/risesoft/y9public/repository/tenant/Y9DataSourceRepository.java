package net.risesoft.y9public.repository.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.tenant.Y9DataSource;

@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9DataSourceRepository
    extends JpaRepository<Y9DataSource, String>, JpaSpecificationExecutor<Y9DataSource> {

    Y9DataSource findByJndiName(String jndiName);
}
