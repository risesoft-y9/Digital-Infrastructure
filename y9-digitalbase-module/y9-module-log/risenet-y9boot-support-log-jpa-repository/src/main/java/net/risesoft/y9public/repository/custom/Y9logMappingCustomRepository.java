package net.risesoft.y9public.repository.custom;

import org.springframework.data.domain.Page;

import net.risesoft.y9public.entity.Y9logMapping;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logMappingCustomRepository {

    Page<Y9logMapping> pageSearchList(Integer page, Integer rows, String modularName, String modularCnName);

    String getCnModularName(String modularName);
}
