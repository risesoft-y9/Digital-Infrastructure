package net.risesoft.service.relation.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.relation.Y9SystemVendor;
import net.risesoft.repository.relation.Y9SystemVendorRepository;
import net.risesoft.service.relation.Y9SystemVendorService;

/**
 * 系统开发商和系统关联 Service 实现
 *
 * @author shidaobang
 * @date 2026/8/31
 */
@Service
@RequiredArgsConstructor
public class Y9SystemVendorServiceImpl implements Y9SystemVendorService {

    private final Y9SystemVendorRepository y9SystemVendorRepository;

    @Override
    @Transactional
    public void deleteByManagerId(String managerId) {
        y9SystemVendorRepository.deleteByManagerId(managerId);
    }

    @Override
    @Transactional
    public void deleteByManagerIdAndSystemId(String managerId, String systemId) {
        y9SystemVendorRepository.deleteByManagerIdAndSystemId(managerId, systemId);
    }

    @Override
    @Transactional
    public void deleteBySystemId(String systemId) {
        y9SystemVendorRepository.deleteBySystemId(systemId);
    }

    @Override
    public List<String> listSystemIdByManagerId(String managerId) {
        return y9SystemVendorRepository.findByManagerId(managerId)
            .stream()
            .map(Y9SystemVendor::getSystemId)
            .distinct()
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void save(String managerId, List<String> systemIds) {
        for (String systemId : systemIds) {
            if (!y9SystemVendorRepository.existsByManagerIdAndSystemId(managerId, systemId)) {
                y9SystemVendorRepository.save(new Y9SystemVendor(managerId, systemId));
            }
        }
    }
}
