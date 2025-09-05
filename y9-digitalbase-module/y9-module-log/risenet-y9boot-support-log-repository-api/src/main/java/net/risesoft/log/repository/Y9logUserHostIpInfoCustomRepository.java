package net.risesoft.log.repository;

import java.util.List;

import net.risesoft.log.domain.Y9LogUserHostIpInfoDO;

/**
 * @author shidaobang
 * @date 2025/09/04
 */
public interface Y9logUserHostIpInfoCustomRepository {

    List<Y9LogUserHostIpInfoDO> findByUserHostIp(String userHostIp);

    List<Y9LogUserHostIpInfoDO> findByClientIpSection(String clientIpSection);

    List<Y9LogUserHostIpInfoDO> findByUserHostIpStartingWith(String userHostIp);

    void save(Y9LogUserHostIpInfoDO y9LogUserHostIpInfoDO);

    List<Y9LogUserHostIpInfoDO> findAll();

}
