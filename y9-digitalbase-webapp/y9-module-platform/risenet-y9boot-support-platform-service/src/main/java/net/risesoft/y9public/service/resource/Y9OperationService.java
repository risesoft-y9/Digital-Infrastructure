package net.risesoft.y9public.service.resource;

import net.risesoft.y9public.entity.resource.Y9Operation;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9OperationService extends ResourceCommonService<Y9Operation> {

    /**
     * 根据父节点id，获取最大排序
     *
     * @param parentId
     * @return
     */
    Integer getMaxIndexByParentId(String parentId);

    /**
     * 移动
     *
     * @param id 按钮资源id
     * @param parentId 父资源id
     * @return {@link Y9Operation}
     */
    Y9Operation move(String id, String parentId);

}
