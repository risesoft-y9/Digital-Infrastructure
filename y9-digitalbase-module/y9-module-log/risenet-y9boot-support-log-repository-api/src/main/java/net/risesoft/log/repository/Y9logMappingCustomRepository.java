package net.risesoft.log.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.risesoft.log.domain.Y9LogMappingDO;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logMappingCustomRepository {

    Page<Y9LogMappingDO> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName);

    String getCnModularName(String modularName);

    void deleteById(String id);

    Optional<Y9LogMappingDO> findById(String id);

    void save(Y9LogMappingDO y9LogMappingDO);

    List<Y9LogMappingDO> findByModularName(String name);

    Page<Y9LogMappingDO> page(Pageable pageable);
}
