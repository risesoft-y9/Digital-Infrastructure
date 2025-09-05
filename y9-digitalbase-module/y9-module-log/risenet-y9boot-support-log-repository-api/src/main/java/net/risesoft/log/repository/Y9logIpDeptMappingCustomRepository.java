package net.risesoft.log.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import net.risesoft.log.domain.Y9LogIpDeptMappingDO;
import net.risesoft.pojo.Y9Page;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logIpDeptMappingCustomRepository {

    Y9Page<Y9LogIpDeptMappingDO> pageSearchList(int page, int rows, String clientIpSection, String deptName);

    List<Y9LogIpDeptMappingDO> findByClientIpSection(String clientIpSection);

    List<Y9LogIpDeptMappingDO> findByTenantIdAndClientIpSection(String tenantId, String clientIpSection);

    void deleteById(String id);

    Y9LogIpDeptMappingDO save(Y9LogIpDeptMappingDO y9LogIpDeptMappingDO);

    List<Y9LogIpDeptMappingDO> findByTenantId(String tenantId, Sort sort);

    List<Y9LogIpDeptMappingDO> findAll(Sort sort);

    Optional<Y9LogIpDeptMappingDO> findById(String id);

    Page<Y9LogIpDeptMappingDO> page(Pageable pageable);
}