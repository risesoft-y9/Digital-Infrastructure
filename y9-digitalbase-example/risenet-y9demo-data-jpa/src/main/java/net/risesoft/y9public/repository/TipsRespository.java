package net.risesoft.y9public.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Tips;

@Repository
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface TipsRespository extends JpaRepository<Tips, Serializable>, JpaSpecificationExecutor<Tips> {

    Tips findByUserId(String userId);
}
