package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9LogUserHostIpInfo;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9LogUserHostIpInfoRepository
    extends JpaRepository<Y9LogUserHostIpInfo, String>, JpaSpecificationExecutor<Y9LogUserHostIpInfo> {

    List<Y9LogUserHostIpInfo> findByClientIpSection(String clientIpSection);

    List<Y9LogUserHostIpInfo> findByUserHostIp(String userHostIp);

    List<Y9LogUserHostIpInfo> findByUserHostIpStartingWith(String userHostIp);

}
