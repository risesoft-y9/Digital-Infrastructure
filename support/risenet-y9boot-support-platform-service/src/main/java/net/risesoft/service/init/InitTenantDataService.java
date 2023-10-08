package net.risesoft.service.init;

/**
 * 初始化租户数据 Service
 * 
 * @author shidaobang
 * @date 2023/10/08
 * @since 9.6.3
 */
public interface InitTenantDataService {

    /**
     * 给某个租户进行数据的初始化
     *
     * @param tenantId 租户id
     */
    void init(String tenantId);

}
