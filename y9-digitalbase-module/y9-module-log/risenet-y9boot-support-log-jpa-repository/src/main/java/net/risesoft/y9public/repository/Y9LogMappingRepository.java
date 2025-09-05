package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9LogMapping;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9LogMappingRepository
    extends JpaRepository<Y9LogMapping, String>, JpaSpecificationExecutor<Y9LogMapping> {

    List<Y9LogMapping> findByModularName(String modularName);
}
