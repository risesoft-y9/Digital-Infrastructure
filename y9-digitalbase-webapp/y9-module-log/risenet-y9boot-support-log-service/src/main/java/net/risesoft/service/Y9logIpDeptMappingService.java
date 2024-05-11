package net.risesoft.service;

import java.util.List;

import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.entity.Y9logIpDeptMapping;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logIpDeptMappingService {

    Y9logIpDeptMapping getById(String id);

    List<Y9logIpDeptMapping> listAll();

    List<Y9logIpDeptMapping> listAllOrderByClientIpSection();

    List<Y9logIpDeptMapping> listByClientIpSection(String clientIpSection);

    List<String> listClientIpSections();

    Y9Page<Y9logIpDeptMapping> pageSearchList(int page, int rows, String clientIpSection, String deptName);

    void removeOrganWords(String[] ipDeptMappingIds);

    void save(Y9logIpDeptMapping y9logIpDeptMapping);

    Y9logIpDeptMapping saveOrUpdate(Y9logIpDeptMapping y9logIpDeptMapping);

    void update4Order(String[] idAndTabIndexs);
}