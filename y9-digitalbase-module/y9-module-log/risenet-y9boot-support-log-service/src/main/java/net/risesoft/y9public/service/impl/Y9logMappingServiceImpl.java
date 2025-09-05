package net.risesoft.y9public.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.log.domain.Y9LogMappingDO;
import net.risesoft.log.repository.Y9logMappingCustomRepository;
import net.risesoft.y9public.service.Y9logMappingService;

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
public class Y9logMappingServiceImpl implements Y9logMappingService {

    private final Y9logMappingCustomRepository y9logMappingCustomRepository;

    @Override
    @Transactional(readOnly = false)
    public void deleteFieldMapping(String id) {
        y9logMappingCustomRepository.deleteById(id);
    }

    @Override
    public String getCnModularName(String modularName) {
        return y9logMappingCustomRepository.getCnModularName(modularName);
    }

    @Override
    public Y9LogMappingDO getFieldMappingEntity(String id) {
        return y9logMappingCustomRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Y9LogMappingDO> page(int page, int rows, String sort) {
        if (StringUtils.isNotEmpty(sort)) {
            Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, sort));
            return y9logMappingCustomRepository.page(pageable);
        }
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, "modularName"));
        return y9logMappingCustomRepository.page(pageable);
    }

    @Override
    public Page<Y9LogMappingDO> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName) {
        return y9logMappingCustomRepository.pageSearchList(page, rows, modularName, modularCnName);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Y9LogMappingDO y9LogMappingDO) {
        y9logMappingCustomRepository.save(y9LogMappingDO);
    }

    @Override
    public List<Y9LogMappingDO> validateName(String name) {
        return y9logMappingCustomRepository.findByModularName(name);
    }

}
