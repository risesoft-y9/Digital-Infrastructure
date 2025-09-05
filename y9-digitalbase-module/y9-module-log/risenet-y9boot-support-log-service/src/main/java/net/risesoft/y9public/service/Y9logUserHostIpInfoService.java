package net.risesoft.y9public.service;

import java.util.List;

import net.risesoft.log.domain.Y9LogUserHostIpInfoDO;

/**
 * 人员登录ip记录管理
 * 
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logUserHostIpInfoService {

    /**
     * 获取全部终端IP
     * 
     * @return {@code List<String>}
     */
    List<String> listAllUserHostIps();

    /**
     * 根据登录用户机器IP，获取人员登录终端记录
     * 
     * @param userHostIp 登录用户机器IP
     * @return {@code List<Y9logUserHostIpInfo>}
     */
    List<Y9LogUserHostIpInfoDO> listByUserHostIp(String userHostIp);

    /**
     * 根据clientIp的ABC段,获取人员登录终端记录
     * 
     * @param clientIpSection clientIp的ABC段
     * @return {@code List<Y9logUserHostIpInfo>}
     */
    List<Y9LogUserHostIpInfoDO> listUserHostIpByClientIpSection(String clientIpSection);

    /**
     * 模糊搜索终端IP列表
     * 
     * @param userHostIp 登录用户机器IP
     * @return {@code List<Y9logUserHostIpInfo>}
     */
    List<String> listUserHostIpByUserHostIpLike(String userHostIp);

    /**
     * 保存人员登录ip记录
     * 
     * @param y9LogUserHostIpInfoDO 人员登录ip记录
     */
    void save(Y9LogUserHostIpInfoDO y9LogUserHostIpInfoDO);
}
