package net.risesoft.y9public.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.y9public.entity.Y9logUserHostIpInfo;

@Transactional(value = "rsPublicTransactionManager", readOnly = true)
public interface Y9logUserHostIpInfoRepository
    extends JpaRepository<Y9logUserHostIpInfo, String>, JpaSpecificationExecutor<Y9logUserHostIpInfo> {

    List<Y9logUserHostIpInfo> findByClientIpSection(String clientIpSection);

    List<Y9logUserHostIpInfo> findByUserHostIp(String userHostIp);

    List<Y9logUserHostIpInfo> findByUserHostIpStartingWith(String userHostIp);

}
