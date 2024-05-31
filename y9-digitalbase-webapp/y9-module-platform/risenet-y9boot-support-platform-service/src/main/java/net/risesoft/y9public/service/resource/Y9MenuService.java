package net.risesoft.y9public.service.resource;

import java.util.List;

import net.risesoft.y9public.entity.resource.Y9Menu;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9MenuService extends ResourceCommonService<Y9Menu> {

    /**
     * 根据父资源id获取其下子资源
     *
     * @param parentId
     * @return
     */
    List<Y9Menu> findByParentId(String parentId);

    /**
     * 根据父节点id，获取最大排序
     *
     * @param parentId
     * @return
     */
    Integer getMaxIndexByParentId(String parentId);

}
