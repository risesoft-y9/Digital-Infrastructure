package net.risesoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9Job;

/**
 * @author sdb
 * @author ls
 * @date 2022/9/22
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9JobRepository extends JpaRepository<Y9Job, String>, JpaSpecificationExecutor<Y9Job> {

    int countByName(String name);

    List<Y9Job> findByNameContainingOrderByTabIndex(String name);

    Y9Job findTopByOrderByTabIndexDesc();

}
