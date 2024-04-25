package net.risesoft.service;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.y9public.entity.Y9logMapping;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logMappingService {

    void deleteFieldMapping(String id);

    String getCnModularName(String modularName);

    Y9logMapping getFieldMappingEntity(String id);

    Page<Y9logMapping> page(int page, int rows, String sort);

    Page<Y9logMapping> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName);

    void save(Y9logMapping y9logMapping);

    List<Y9logMapping> validateName(String name);
}
