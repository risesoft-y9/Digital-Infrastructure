package net.risesoft.service.idcode.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.entity.idcode.Y9IdCode;
import net.risesoft.repository.idcode.Y9IdCodeRepository;
import net.risesoft.service.idcode.Y9IdCodeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 统一码服务实现
 * @author : qinman
 * @date : 2024-06-12
 **/
@Component
@Slf4j
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9IdCodeServiceImpl implements Y9IdCodeService {

    private final Y9IdCodeRepository y9IdCodeRepository;

    @Override
    public Y9IdCode findById(String id) {
        return y9IdCodeRepository.findById(id).orElse(null);
    }

    @Override
    public Y9IdCode findByOrgUnitId(String orgUnitId) {
        return y9IdCodeRepository.findByOrgUnitId(orgUnitId);
    }

    @Override
    @Transactional
    public Y9IdCode save(Y9IdCode idCode) {
        return y9IdCodeRepository.save(idCode);
    }
}
