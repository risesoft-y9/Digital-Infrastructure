package net.risesoft.log.service;

import java.util.List;

import net.risesoft.log.entity.Y9logIpDeptMapping;
import net.risesoft.pojo.Y9Page;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logIpDeptMappingService {

    public Y9logIpDeptMapping getById(String id);

    public List<Y9logIpDeptMapping> listAll();

    public List<Y9logIpDeptMapping> listAllOrderByClientIpSection();

    public List<Y9logIpDeptMapping> listByClientIpSection(String clientIpSection);

    public List<String> listClientIpSections();

    public Y9Page<Y9logIpDeptMapping> pageSearchList(int page, int rows, String clientIpSection, String deptName);

    public void removeOrganWords(String[] ipDeptMappingIds);

    public void save(Y9logIpDeptMapping y9logIpDeptMapping);

    public Y9logIpDeptMapping saveOrUpdate(Y9logIpDeptMapping y9logIpDeptMapping);

    public void update4Order(String[] idAndTabIndexs);
}