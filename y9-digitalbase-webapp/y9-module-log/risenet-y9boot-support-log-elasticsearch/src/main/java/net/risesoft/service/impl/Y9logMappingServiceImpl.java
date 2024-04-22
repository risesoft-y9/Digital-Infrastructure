package net.risesoft.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.service.Y9logMappingService;
import net.risesoft.y9public.entity.Y9logMapping;
import net.risesoft.y9public.repository.Y9logMappingRepository;
import net.risesoft.y9public.repository.custom.Y9logMappingCustomRepository;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Service
@RequiredArgsConstructor
public class Y9logMappingServiceImpl implements Y9logMappingService {

    private final Y9logMappingRepository y9logMappingRepository;

    private final Y9logMappingCustomRepository y9logMappingCustomRepository;

    @Override
    public void deleteFieldMapping(String id) {
        y9logMappingRepository.deleteById(id);
    }

    @Override
    public String getCnModularName(String modularName) {
        return y9logMappingCustomRepository.getCnModularName(modularName);
    }

    @Override
    public Y9logMapping getFieldMappingEntity(String id) {
        return y9logMappingRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Y9logMapping> page(int page, int rows, String sort) {
        if (sort != null && !"".equals(sort)) {
            Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, sort));
            return y9logMappingRepository.findAll(pageable);
        }
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, "modularName"));
        return y9logMappingRepository.findAll(pageable);
    }

    @Override
    public Page<Y9logMapping> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName) {

        return y9logMappingCustomRepository.pageSearchList(page, rows, modularName, modularCnName);
    }

    @Override
    public void save(Y9logMapping y9logMapping) {
        y9logMappingRepository.save(y9logMapping);
    }

    @Override
    public List<Y9logMapping> validateName(String name) {
        return y9logMappingRepository.findByModularName(name);
    }

}
