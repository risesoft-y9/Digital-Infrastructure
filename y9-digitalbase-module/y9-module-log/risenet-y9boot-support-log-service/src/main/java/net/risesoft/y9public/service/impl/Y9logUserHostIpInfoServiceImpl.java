package net.risesoft.y9public.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.domain.Y9LogUserHostIpInfoDO;
import net.risesoft.log.repository.Y9logUserHostIpInfoCustomRepository;
import net.risesoft.y9public.service.Y9logUserHostIpInfoService;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Y9logUserHostIpInfoServiceImpl implements Y9logUserHostIpInfoService {

    private final Y9logUserHostIpInfoCustomRepository y9logUserHostIpInfoCustomRepository;

    @Override
    public List<String> listAllUserHostIps() {
        return y9logUserHostIpInfoCustomRepository.findAll()
            .stream()
            .map(Y9LogUserHostIpInfoDO::getUserHostIp)
            .distinct()
            .collect(Collectors.toList());
    }

    @Override
    public List<Y9LogUserHostIpInfoDO> listByUserHostIp(String userHostIp) {
        return y9logUserHostIpInfoCustomRepository.findByUserHostIp(userHostIp);
    }

    @Override
    public List<Y9LogUserHostIpInfoDO> listUserHostIpByClientIpSection(String clientIpSection) {
        return y9logUserHostIpInfoCustomRepository.findByClientIpSection(clientIpSection);
    }

    @Override
    public List<String> listUserHostIpByUserHostIpLike(String userHostIp) {
        List<Y9LogUserHostIpInfoDO> list = y9logUserHostIpInfoCustomRepository.findByUserHostIpStartingWith(userHostIp);
        return list.stream().map(Y9LogUserHostIpInfoDO::getUserHostIp).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Y9LogUserHostIpInfoDO y9LogUserHostIpInfoDO) {
        y9logUserHostIpInfoCustomRepository.save(y9LogUserHostIpInfoDO);
    }
}
