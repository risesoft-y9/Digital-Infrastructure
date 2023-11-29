package y9.autoconfiguration.liquibase;

/**
 * 租户租用系统数据结构初始化后需要进行的数据初始化 <br/>
 * 系统可根据自己的需求实现接口做数据初始化
 * 
 * @author shidaobang
 * @date 2023/11/20
 * @since 9.6.3
 */
public interface TenantDataInitializer {

    void init(String tenantId);

}
