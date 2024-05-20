package net.risesoft.y9public.repository.custom;

import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.entity.Y9logIpDeptMapping;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9logIpDeptMappingCustomRepository {

    Y9Page<Y9logIpDeptMapping> pageSearchList(int page, int rows, String clientIpSection, String deptName);
}