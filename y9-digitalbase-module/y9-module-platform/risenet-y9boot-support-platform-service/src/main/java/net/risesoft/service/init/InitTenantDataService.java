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
     * 给某个租户进行数据的初始化，使用之前需先调用 Y9LoginUserHolder.setTenantId 设置租户 id 以便使用对应的数据源
     */
    void initAll();

    /**
     * 初始化三员
     */
    void initManagers();

    /**
     * 初始化数据字典
     */
    void initOptionClass();
}
