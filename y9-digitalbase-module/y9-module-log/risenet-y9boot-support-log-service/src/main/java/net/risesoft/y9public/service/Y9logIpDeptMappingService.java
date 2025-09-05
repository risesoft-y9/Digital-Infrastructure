package net.risesoft.y9public.service;

import java.util.List;

import net.risesoft.log.domain.Y9LogIpDeptMappingDO;
import net.risesoft.pojo.Y9Page;

/**
 * 人员登录部门配置管理
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logIpDeptMappingService {

    List<Y9LogIpDeptMappingDO> listAllOrderByClientIpSection();

    List<Y9LogIpDeptMappingDO> listByClientIpSection(String clientIpSection);

    List<Y9LogIpDeptMappingDO> listByTenantIdAndClientIpSection(String tenantId, String clientIpSection);

    List<String> listClientIpSections();

    Y9Page<Y9LogIpDeptMappingDO> pageSearchList(int page, int rows, String clientIpSection, String deptName);

    void removeOrganWords(String[] ipDeptMappingIds);

    void save(Y9LogIpDeptMappingDO y9LogIpDeptMappingDO);

    Y9LogIpDeptMappingDO saveOrUpdate(Y9LogIpDeptMappingDO y9LogIpDeptMappingDO);

    void update4Order(String[] idAndTabIndexs);
}