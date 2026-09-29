package net.risesoft.service.idcode;

import java.util.Optional;

import net.risesoft.model.platform.IdCode;

public interface Y9IdCodeService {

    /**
     * 根据id查找统一码
     *
     * @param id 唯一标识
     * @return {@code Optional<IdCode>}
     */
    Optional<IdCode> findById(String id);

    /**
     * 根据组织节点id查找统一码
     *
     * @param orgUnitId 组织节点id
     * @return {@code Optional<IdCode>}
     */
    Optional<IdCode> findByOrgUnitId(String orgUnitId);

    /**
     * 保存统一码
     *
     * @param idCode 统一码
     * @return {@link IdCode}
     */
    IdCode save(IdCode idCode);
}
