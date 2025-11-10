package net.risesoft.repository.idcode;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.risesoft.entity.idcode.Y9IdCode;

@Repository
public interface Y9IdCodeRepository extends JpaRepository<Y9IdCode, String>, JpaSpecificationExecutor<Y9IdCode> {

    Optional<Y9IdCode> findByOrgUnitId(String orgUnitId);
}
