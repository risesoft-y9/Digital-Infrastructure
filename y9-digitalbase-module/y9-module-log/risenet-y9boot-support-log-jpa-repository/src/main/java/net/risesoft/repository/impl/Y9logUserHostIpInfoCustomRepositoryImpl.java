package net.risesoft.repository.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.domain.Y9LogUserHostIpInfoDO;
import net.risesoft.log.repository.Y9logUserHostIpInfoCustomRepository;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9LogUserHostIpInfo;
import net.risesoft.y9public.repository.Y9LogUserHostIpInfoRepository;

/**
 * @author shidaobang
 * @date 2025/09/04
 */
@Component
@RequiredArgsConstructor
public class Y9logUserHostIpInfoCustomRepositoryImpl implements Y9logUserHostIpInfoCustomRepository {

    private final Y9LogUserHostIpInfoRepository y9logUserHostIpInfoRepository;

    @Override
    public List<Y9LogUserHostIpInfoDO> findByUserHostIp(String userHostIp) {
        return y9logUserHostIpInfoRepository.findByUserHostIp(userHostIp)
            .stream()
            .map(h -> Y9ModelConvertUtil.convert(h, Y9LogUserHostIpInfoDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<Y9LogUserHostIpInfoDO> findByClientIpSection(String clientIpSection) {
        return y9logUserHostIpInfoRepository.findByClientIpSection(clientIpSection)
            .stream()
            .map(h -> Y9ModelConvertUtil.convert(h, Y9LogUserHostIpInfoDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<Y9LogUserHostIpInfoDO> findByUserHostIpStartingWith(String userHostIp) {
        return y9logUserHostIpInfoRepository.findByUserHostIpStartingWith(userHostIp)
            .stream()
            .map(h -> Y9ModelConvertUtil.convert(h, Y9LogUserHostIpInfoDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public void save(Y9LogUserHostIpInfoDO y9LogUserHostIpInfoDO) {
        Y9LogUserHostIpInfo y9LogUserHostIpInfo =
            Y9ModelConvertUtil.convert(y9LogUserHostIpInfoDO, Y9LogUserHostIpInfo.class);
        y9logUserHostIpInfoRepository.save(y9LogUserHostIpInfo);
    }

    @Override
    public List<Y9LogUserHostIpInfoDO> findAll() {
        return y9logUserHostIpInfoRepository.findAll()
            .stream()
            .map(h -> Y9ModelConvertUtil.convert(h, Y9LogUserHostIpInfoDO.class))
            .collect(Collectors.toList());
    }
}
