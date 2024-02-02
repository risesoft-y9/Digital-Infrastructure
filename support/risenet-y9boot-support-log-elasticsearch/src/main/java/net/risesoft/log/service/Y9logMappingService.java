package net.risesoft.log.service;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.log.entity.Y9logMapping;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logMappingService {

    public void deleteFieldMapping(String id);

    public String getCnModularName(String modularName);

    public Y9logMapping getFieldMappingEntity(String id);

    public Page<Y9logMapping> page(int page, int rows, String sort);

    public Page<Y9logMapping> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName);

    public void save(Y9logMapping y9logMapping);

    public List<Y9logMapping> validateName(String name);
}
