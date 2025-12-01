package net.risesoft.y9public.service.resource;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.resource.App;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9AppService extends ResourceCommonService<App> {

    /**
     * 验证该应用是否已存在
     *
     * @param systemId 系统的唯一标识
     * @param name 应用名
     * @return boolean
     */
    boolean existBySystemIdAndName(String systemId, String name);

    /**
     * 验证应用的链接地址是否已存在
     *
     * @param systemId 系统id
     * @param url 链接地址
     * @return boolean
     */
    boolean existBySystemIdAndUrl(String systemId, String url);

    /**
     * 根据系统唯一标示和自定义标识查找应用
     *
     * @param systemId 系统id
     * @param customId 自定义id
     * @return {@code Optional<}{@link App}{@code >}
     */
    Optional<App> findBySystemIdAndCustomId(String systemId, String customId);

    /**
     * 根据系统名称和自定义id查找应用
     *
     * @param systemName 系统名
     * @param customId 自定义id
     * @return {@code Optional<}{@link App}{@code >}
     */
    Optional<App> findBySystemNameAndCustomId(String systemName, String customId);

    /**
     * 查询所有App
     *
     * @return {@code List<App>}
     */
    List<App> listAll();

    /**
     * 根据appName，查询应用
     *
     * @param appName 应用名
     * @return {@code List<App>}
     */
    List<App> listByAppName(String appName);

    /**
     * 获取应用列表
     *
     * @param autoInit 是否自动租用
     * @param checked 是否审核通过
     * @return {@code List<App>}
     */
    List<App> listByAutoInitAndChecked(Boolean autoInit, Boolean checked);

    /**
     * 查询所有审核通过的应用
     *
     * @param checked 是否已审核
     * @return {@code List<App>}
     */
    List<App> listByChecked(boolean checked);

    /**
     * 根据自定义标示查找应用列表
     *
     * @param customId 自定义id
     * @return {@code List<App>}
     */
    List<App> listByCustomId(String customId);

    List<App> listByEnable();

    List<App> listByIds(List<String> appIdList);

    /**
     * 根据systemId，获取应用列表
     *
     * @param systemId 系统id
     * @return {@code List<App>}
     */
    List<App> listBySystemId(String systemId);

    /**
     * 根据系统名称查找应用
     *
     * @param systemName 系统名
     * @return {@code List<App>}
     */
    List<App> listBySystemName(String systemName);

    /**
     * 根据系统id和名称分页查询系统
     *
     * @param pageQuery 分页查询
     * @param systemId 系统id
     * @param name 系统名称
     * @return {@code Page<App>}
     */
    Y9Page<App> page(Y9PageQuery pageQuery, String systemId, String name);

    App saveAndRegister4Tenant(App y9App);

    /**
     * 保存isv应用
     *
     * @param app 应用程序
     * @return {@link App}
     */
    App saveIsvApp(App app);

    /**
     * 保存应用排序
     *
     * @param appIds 应用ids
     */
    void saveOrder(String[] appIds);

    /**
     * 审核应用
     *
     * @param id id
     * @param checked 是否已审核
     * @param verifyUserName 审核人
     * @return {@link App}
     */
    App verifyApp(String id, boolean checked, String verifyUserName);

    void deleteAfterCheck(String id);
}
