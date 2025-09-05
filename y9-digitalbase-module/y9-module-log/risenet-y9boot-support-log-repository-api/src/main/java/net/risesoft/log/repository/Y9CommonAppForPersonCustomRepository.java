package net.risesoft.log.repository;

import java.util.List;

import net.risesoft.log.domain.Y9CommonAppForPersonDO;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
public interface Y9CommonAppForPersonCustomRepository {

    List<String> getAppNamesByPersonId(String personId);

    long getCount();

    Y9CommonAppForPersonDO findByPersonId(String personId);

    void save(Y9CommonAppForPersonDO cafp);
}
