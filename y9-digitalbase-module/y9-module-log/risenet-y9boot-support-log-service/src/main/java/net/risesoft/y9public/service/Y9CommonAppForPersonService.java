package net.risesoft.y9public.service;

import java.util.List;

import net.risesoft.log.domain.Y9CommonAppForPersonDO;

/**
 * 个人常用应用信息管理
 * 
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9CommonAppForPersonService {

    /**
     * 根据用户id，获取个人常用名称列表
     * 
     * @param personId 用户id
     * @return {@code List<String>}
     */
    List<String> getAppNamesByPersonId(String personId);

    Y9CommonAppForPersonDO getCommonAppForPersonByPersonId(String personId);

    long getCount();

    void saveOrUpdate(Y9CommonAppForPersonDO cafp);

}
