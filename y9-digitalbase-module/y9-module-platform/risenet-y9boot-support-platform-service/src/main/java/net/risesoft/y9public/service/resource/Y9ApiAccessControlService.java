package net.risesoft.y9public.service.resource;

import java.util.List;

import net.risesoft.enums.platform.ApiAccessControlType;
import net.risesoft.model.platform.ApiAccessControl;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * api 访问控制 service
 *
 * @author shidaobang
 * @date 2024/11/26
 * @since 9.6.8
 */
public interface Y9ApiAccessControlService {

    /**
     * 根据访问控制类型查询列表
     *
     * @param type 访问控制类型
     * @return {@code List<ApiAccessControl>}
     */
    List<ApiAccessControl> listByType(ApiAccessControlType type);

    /**
     * 保存或更新 API 访问控制
     *
     * @param apiAccessControl API 访问控制对象
     * @return {@link ApiAccessControl}
     * @throws Y9BusinessException IP 地址格式不正确的情况
     */
    ApiAccessControl saveOrUpdate(ApiAccessControl apiAccessControl);

    /**
     * 根据 id 删除 API 访问控制
     *
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 修改 API 访问控制的启用状态
     *
     * @param id 唯一标识
     * @return {@link ApiAccessControl}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    ApiAccessControl changeEnabled(String id);

    /**
     * 保存应用 id 和密钥
     *
     * @param apiAccessControl API 访问控制对象
     * @return {@link ApiAccessControl}
     */
    ApiAccessControl saveAppIdSecret(ApiAccessControl apiAccessControl);
}
