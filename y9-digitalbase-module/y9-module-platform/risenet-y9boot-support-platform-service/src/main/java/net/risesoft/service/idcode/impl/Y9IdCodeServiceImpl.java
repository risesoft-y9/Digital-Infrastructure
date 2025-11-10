package net.risesoft.service.idcode.impl;

import java.util.Optional;

import net.risesoft.util.PlatformModelConvertUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.idcode.Y9IdCode;
import net.risesoft.model.platform.IdCode;
import net.risesoft.repository.idcode.Y9IdCodeRepository;
import net.risesoft.service.idcode.Y9IdCodeService;

/**
 * 统一码服务实现
 *
 * @author : qinman
 * @date : 2024-06-12
 **/
@Component
@Slf4j
@RequiredArgsConstructor
public class Y9IdCodeServiceImpl implements Y9IdCodeService {

    private final Y9IdCodeRepository y9IdCodeRepository;

    @Override
    public Optional<IdCode> findById(String id) {
        return y9IdCodeRepository.findById(id).map(Y9IdCodeServiceImpl::entityToModel);
    }

    private static IdCode entityToModel(Y9IdCode y9IdCode) {
        return PlatformModelConvertUtil.convert(y9IdCode, IdCode.class);
    }

    @Override
    public Optional<IdCode> findByOrgUnitId(String orgUnitId) {
        return y9IdCodeRepository.findByOrgUnitId(orgUnitId).map(Y9IdCodeServiceImpl::entityToModel);
    }

    @Override
    @Transactional
    public IdCode save(IdCode idCode) {
        Y9IdCode y9IdCode = PlatformModelConvertUtil.convert(idCode, Y9IdCode.class);
        return entityToModel(y9IdCodeRepository.save(y9IdCode));
    }

}
