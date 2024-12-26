package net.risesoft.y9public.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.y9public.entity.Y9logUserHostIpInfo;
import net.risesoft.y9public.repository.Y9logUserHostIpInfoRepository;
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

    private final Y9logUserHostIpInfoRepository y9logUserHostIpInfoRepository;

    @Override
    public List<String> listAllUserHostIps() {
        Iterator<Y9logUserHostIpInfo> userHostIpIterator = y9logUserHostIpInfoRepository.findAll().iterator();
        Set<String> list = new HashSet<>();
        while (userHostIpIterator.hasNext()) {
            list.add(userHostIpIterator.next().getUserHostIp());
        }
        List<String> userHostIpList = new ArrayList<>();
        userHostIpList.addAll(list);
        return userHostIpList;
    }

    @Override
    public List<Y9logUserHostIpInfo> listByUserHostIp(String userHostIp) {
        return y9logUserHostIpInfoRepository.findByUserHostIp(userHostIp);
    }

    @Override
    public List<Y9logUserHostIpInfo> listUserHostIpByClientIpSection(String clientIpSection) {
        return y9logUserHostIpInfoRepository.findByClientIpSection(clientIpSection);
    }

    @Override
    public List<String> listUserHostIpByUserHostIpLike(String userHostIp) {
        List<Y9logUserHostIpInfo> list = y9logUserHostIpInfoRepository.findByUserHostIpStartingWith(userHostIp);
        return list.stream().map(Y9logUserHostIpInfo::getUserHostIp).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Y9logUserHostIpInfo y9logUserHostIpInfo) {
        y9logUserHostIpInfoRepository.save(y9logUserHostIpInfo);
    }
}
