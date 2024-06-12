package net.risesoft.service.idcode;

import net.risesoft.entity.idcode.Y9IdCode;

public interface Y9IdCodeService {

    Y9IdCode findById(String id);

    Y9IdCode findByOrgUnitId(String orgUnitId);

    Y9IdCode save(Y9IdCode idCode);
}
