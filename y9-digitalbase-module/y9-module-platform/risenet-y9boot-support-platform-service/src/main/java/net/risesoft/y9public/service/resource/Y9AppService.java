package net.risesoft.y9public.service.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9public.entity.resource.Y9App;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9AppService extends ResourceCommonService<Y9App> {

    /**
     * 根据系统id查相关应用的数量
     *
     * @param systemId 系统id
     * @return long
     */
    long countBySystemId(String systemId);

    /**
     * 根据系统Id禁用该系统所有的应用
     *
     * @param systemId 系统id
     */
    void disableBySystemId(String systemId);

    /**
     * 根据系统Id启用该系统所有的应用
     *
     * @param systemId 系统id
     */
    void enableBySystemId(String systemId);

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
     * @return {@code Optional<}{@link Y9App}{@code >}
     */
    Optional<Y9App> findBySystemIdAndCustomId(String systemId, String customId);

    /**
     * 根据系统名称和自定义id查找应用
     *
     * @param systemName 系统名
     * @param customId 自定义id
     * @return {@code Optional<}{@link Y9App}{@code >}
     */
    Optional<Y9App> findBySystemNameAndCustomId(String systemName, String customId);

    /**
     * 用于工作流 str是资源Id,流程作为应用发不到系统时,url中包含流程在资源树上生成的Id,且包含str的数据是唯一的
     *
     * @param url 链接
     * @return {@code List<Y9App>}
     */
    List<Y9App> findByUrlLike(String url);

    /**
     * 查询所有App
     *
     * @return {@code List<Y9App>}
     */
    List<Y9App> listAll();

    /**
     * 根据appName，查询应用
     *
     * @param appName 应用名
     * @return {@code List<Y9App>}
     */
    List<Y9App> listByAppName(String appName);

    /**
     * 获取应用列表
     *
     * @param autoInit 是否自动租用
     * @param checked 是否审核通过
     * @return {@code List<Y9App>}
     */
    List<Y9App> listByAutoInitAndChecked(Boolean autoInit, Boolean checked);

    /**
     * 查询所有审核通过的应用
     *
     * @param checked 是否已审核
     * @return {@code List<Y9App>}
     */
    List<Y9App> listByChecked(boolean checked);

    /**
     * 根据自定义标示查找应用列表
     *
     * @param customId 自定义id
     * @return {@code List<Y9App>}
     */
    List<Y9App> listByCustomId(String customId);

    List<Y9App> listByEnable();

    /**
     * 根据systemId，获取应用列表
     *
     * @param systemId 系统id
     * @return {@code List<Y9App>}
     */
    List<Y9App> listBySystemId(String systemId);

    /**
     * 根据系统名称查找应用
     *
     * @param systemName 系统名
     * @return {@code List<Y9App>}
     */
    List<Y9App> listBySystemName(String systemName);

    /**
     * 根据系统id和名称分页查询系统
     *
     * @param pageQuery 分页查询
     * @param systemId 系统id
     * @param name 系统名称
     * @return {@code Page<Y9App>}
     */
    Page<Y9App> page(Y9PageQuery pageQuery, String systemId, String name);

    Y9App saveAndRegister4Tenant(Y9App y9App);

    /**
     * 保存isv应用
     *
     * @param app 应用程序
     * @return {@link Y9App}
     */
    Y9App saveIsvApp(Y9App app);

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
     * @return {@link Y9App}
     */
    Y9App verifyApp(String id, boolean checked, String verifyUserName);
}
