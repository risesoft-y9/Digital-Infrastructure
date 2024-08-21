package net.risesoft.y9public.service.resource;

import java.util.List;

import net.risesoft.y9public.entity.resource.Y9Operation;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9OperationService extends ResourceCommonService<Y9Operation> {
    /**
     * 根据父资源id获取其下子资源
     *
     * @param parentId 父节点id
     * @return {@code List<}{@link Y9Operation}{@code >}
     */
    List<Y9Operation> findByParentId(String parentId);

    /**
     * 根据父节点id，获取最大排序
     *
     * @param parentId 父节点id
     * @return Integer
     */
    Integer getMaxIndexByParentId(String parentId);

}
