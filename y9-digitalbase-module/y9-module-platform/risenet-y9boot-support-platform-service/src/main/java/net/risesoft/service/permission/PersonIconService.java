package net.risesoft.service.permission;

import java.util.List;

import net.risesoft.model.platform.PersonIconItem;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;

/**
 * 人员图标 Service
 *
 * @author shidaobang
 * @date 2025/06/24
 */
public interface PersonIconService {

    void buildPersonalAppIconByOrgUnitId(String orgUnitId);

    List<PersonIconItem> listByOrgUnitId(String orgUnitId);

    Y9Page<PersonIconItem> pageByOrgUnitId(String orgUnitId, Y9PageQuery pageQuery);

}
