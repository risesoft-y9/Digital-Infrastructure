package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9logMapping;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9logMappingRepository
    extends JpaRepository<Y9logMapping, String>, JpaSpecificationExecutor<Y9logMapping> {

    List<Y9logMapping> findByModularName(String modularName);
}
