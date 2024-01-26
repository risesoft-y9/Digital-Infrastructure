package net.risesoft.log.service;

import java.util.List;

import net.risesoft.log.entity.Y9CommonAppForPerson;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9CommonAppForPersonService {

    public List<String> getAppNamesByPersonId(String personId);

    public String getAppNamesFromLog(String personId);

    public Y9CommonAppForPerson getCommonAppForPersonByPersonId(String personId);

    public long getCount();

    public String saveForQuery();

    public void saveOrUpdate(Y9CommonAppForPerson cafp);

    public String syncData();
}
