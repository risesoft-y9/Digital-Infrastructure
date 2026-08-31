package net.risesoft.service.relation;

import java.util.List;

/**
 * 系统开发商和系统关联 Service
 *
 * @author shidaobang
 * @date 2026/8/31
 */
public interface Y9SystemVendorService {

    void deleteByManagerId(String managerId);

    void deleteByManagerIdAndSystemId(String managerId, String systemId);

    void deleteBySystemId(String systemId);

    List<String> listSystemIdByManagerId(String managerId);

    void save(String managerId, List<String> systemIds);
}
