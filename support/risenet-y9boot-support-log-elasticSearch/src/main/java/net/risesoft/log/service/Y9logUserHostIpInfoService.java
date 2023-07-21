package net.risesoft.log.service;

import java.util.List;

import net.risesoft.log.entity.Y9logUserHostIpInfo;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logUserHostIpInfoService {

    public List<String> listAllUserHostIps();

    public List<Y9logUserHostIpInfo> listByUserHostIp(String userHostIp);

    public List<Y9logUserHostIpInfo> listUserHostIpByClientIpSection(String clientIpSection);

    public List<String> listUserHostIpByUserHostIpLike(String userHostIp);

    public void save(Y9logUserHostIpInfo y9logUserHostIpInfo);
}
