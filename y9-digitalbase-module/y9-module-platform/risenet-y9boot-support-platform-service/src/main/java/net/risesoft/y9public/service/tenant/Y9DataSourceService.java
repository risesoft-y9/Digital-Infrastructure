package net.risesoft.y9public.service.tenant;

import java.util.Optional;

import javax.sql.DataSource;

import net.risesoft.model.platform.tenant.DataSourceInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9DataSourceService {

    /**
     * 修改密码
     *
     * @param id ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(String id, String oldPassword, String newPassword);

    /**
     * 根据数据库名称创建数据源
     *
     * @param dbName 数据库名称
     * @param createSchema 是否创建数据库、schema
     * @return {@link DataSourceInfo}
     */
    DataSourceInfo createDataSource(String dbName, boolean createSchema);

    /**
     * 根据id删除数据源
     *
     * @param id ：数据源主键id
     */
    void delete(String id);

    /**
     * 检查数据源关联关系后根据 id 删除
     *
     * @param id 数据源 id
     * @throws Y9BusinessException 数据源正在被租户系统使用的情况
     */
    void deleteAfterCheck(String id);

    /**
     * 根据ID获取数据源
     *
     * @param id 数据源主键id
     * @return 数据源对象 或 null
     */
    Optional<DataSourceInfo> findById(String id);

    /**
     * 根据ID获取数据源
     *
     * @param id 数据源主键id
     * @return {@link DataSourceInfo}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    DataSourceInfo getById(String id);

    /**
     * 获取根据ID获取要测试的数据源
     *
     * @param id 唯一标识
     * @return {@link DataSourceInfo}
     */
    DataSource getDataSource(String id);

    /**
     * 查询租户数据源分页列表
     *
     * @param pageQuery 分页信息
     * @return {@code Page<}{@link DataSourceInfo}{@code >}
     */
    Y9Page<DataSourceInfo> page(Y9PageQuery pageQuery);

    /**
     * 保存租户数据源
     *
     * @param y9DataSource 数据源对象
     * @return {@link DataSourceInfo}
     */
    DataSourceInfo save(DataSourceInfo y9DataSource);

    /**
     * 保存租户数据源并发布变更消息
     *
     * @param y9DataSource 数据源对象
     * @return {@link DataSourceInfo}
     */
    DataSourceInfo saveAndPublishMessage(DataSourceInfo y9DataSource);
}
