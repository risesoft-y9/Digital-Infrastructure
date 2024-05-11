package net.risesoft.service;

import java.util.List;

import net.risesoft.y9public.entity.Y9CommonAppForPerson;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9CommonAppForPersonService {

    List<String> getAppNamesByPersonId(String personId);

    Y9CommonAppForPerson getCommonAppForPersonByPersonId(String personId);

    long getCount();

    void saveOrUpdate(Y9CommonAppForPerson cafp);

}
