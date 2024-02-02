package net.risesoft.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Y9PersonExt;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9PersonExtRepository
    extends JpaRepository<Y9PersonExt, String>, JpaSpecificationExecutor<Y9PersonExt> {

    List<Y9PersonExt> findByIdTypeAndIdNum(String idType, String idNum);

    Optional<Y9PersonExt> findByPersonId(String personId);
}
