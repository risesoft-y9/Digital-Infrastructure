package net.risesoft.service;

import java.util.List;

import net.risesoft.y9public.entity.Y9logUserHostIpInfo;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logUserHostIpInfoService {

    List<String> listAllUserHostIps();

    List<Y9logUserHostIpInfo> listByUserHostIp(String userHostIp);

    List<Y9logUserHostIpInfo> listUserHostIpByClientIpSection(String clientIpSection);

    List<String> listUserHostIpByUserHostIpLike(String userHostIp);

    void save(Y9logUserHostIpInfo y9logUserHostIpInfo);
}
