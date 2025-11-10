package net.risesoft.service.idcode;

import java.util.Optional;

import net.risesoft.model.platform.IdCode;

public interface Y9IdCodeService {

    Optional<IdCode> findById(String id);

    Optional<IdCode> findByOrgUnitId(String orgUnitId);

    IdCode save(IdCode idCode);
}
