package net.risesoft.service.impl;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Tenant;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.Y9logAccessLogService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9logAccessLog;
import net.risesoft.y9public.repository.custom.Y9logAccessLogCustomRepository;

import y9.client.rest.platform.org.DepartmentApiClient;
import y9.client.rest.platform.org.GroupApiClient;
import y9.client.rest.platform.org.OrganizationApiClient;
import y9.client.rest.platform.org.PersonApiClient;
import y9.client.rest.platform.org.PositionApiClient;
import y9.client.rest.platform.tenant.TenantApiClient;

/**
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 */
@Service
@RequiredArgsConstructor
public class Y9logAccessLogServiceImpl implements Y9logAccessLogService {

    private final DepartmentApiClient departmentManager;
    private final PersonApiClient personManager;
    private final PositionApiClient positionManager;
    private final GroupApiClient groupManager;
    private final OrganizationApiClient organizationManager;
    private final TenantApiClient tenantManager;

    private final Y9logAccessLogCustomRepository y9logAccessLogCustomRepository;

    @Override
    public Map<String, Object> getAppClickCount(String orgId, String orgType, String tenantId, String startDay,
        String endDay) throws UnknownHostException {
        String guidPath = getGuidPath(orgId, orgType, tenantId);
        return y9logAccessLogCustomRepository.getAppClickCount(tenantId, guidPath, startDay, endDay);
    }

    public String getGuidPath(String orgId, String orgType, String tenantId) {
        String guidPath = null;
        if (StringUtils.isNotBlank(orgId) && StringUtils.isNotBlank(orgType)) {
            if (orgType.equals(OrgTypeEnum.ORGANIZATION.getEnName())) {
                guidPath = organizationManager.get(tenantId, orgId).getData().getGuidPath();

            } else if (orgType.equals(OrgTypeEnum.DEPARTMENT.getEnName())) {
                guidPath = departmentManager.get(tenantId, orgId).getData().getGuidPath();

            } else if (orgType.equals(OrgTypeEnum.GROUP.getEnName())) {
                guidPath = groupManager.get(tenantId, orgId).getData().getGuidPath();

            } else if (orgType.equals(OrgTypeEnum.POSITION.getEnName())) {
                guidPath = positionManager.get(tenantId, orgId).getData().getGuidPath();

            } else if (orgType.equals(OrgTypeEnum.PERSON.getEnName())) {
                guidPath = personManager.get(tenantId, orgId).getData().getGuidPath();
            }
        }
        return guidPath;
    }

    @Override
    public Map<String, Object> getModuleNameCount(String orgId, String orgType, String tenantId, String startDay,
        String endDay) {
        String guidPath = getGuidPath(orgId, orgType, tenantId);
        return y9logAccessLogCustomRepository.getModuleNameCount(tenantId, guidPath, startDay, endDay);
    }

    @Override
    public Map<String, Object> getOperateStatusCount(String selectedDate) {
        return y9logAccessLogCustomRepository.getOperateStatusCount(selectedDate,
            getTenantType(Y9LoginUserHolder.getTenantId()));
    }

    private Integer getTenantType(String tenantId) {
        Integer num = 0;
        Tenant tenant = tenantManager.getById(tenantId).getData();
        if (null != tenant) {
            num = tenant.getTenantType().getValue();
        }
        return num;
    }

    @Override
    public List<String> listAccessLog(String startTime, String endTime, String loginName, String tenantId) {
        return y9logAccessLogCustomRepository.listAccessLog(tenantId, loginName, startTime, endTime);
    }

    @Override
    public List<Long> listOperateTimeCount(String startDay, String endDay) {
        return y9logAccessLogCustomRepository.listOperateTimeCount(startDay, endDay,
            getTenantType(Y9LoginUserHolder.getTenantId()));
    }

    @Override
    public Page<Y9logAccessLog> page(int page, int rows, String sort) {
        return y9logAccessLogCustomRepository.page(page, rows, sort);
    }

    @Override
    public Y9Page<AccessLog> pageByCondition(LogInfoModel searchDto, String startTime, String endTime, Integer page,
        Integer rows) {
        return y9logAccessLogCustomRepository.pageByCondition(searchDto, startTime, endTime, page, rows);
    }

    @Override
    public Y9Page<AccessLog> pageByOperateType(String operateType, Integer page, Integer rows) {
        return y9logAccessLogCustomRepository.pageByOperateType(operateType, page, rows);
    }

    @Override
    public Y9Page<AccessLog> pageByOrgType(String tenantId, String orgId, String orgType, String operateType,
        Integer page, Integer rows) {
        List<String> ids = new ArrayList<>();
        List<Person> allPersons = new ArrayList<>();
        if (orgType.equals(OrgTypeEnum.DEPARTMENT.getEnName())) {
            allPersons = personManager.listRecursivelyByParentId(tenantId, orgId).getData();
        } else if (orgType.equals(OrgTypeEnum.GROUP.getEnName())) {
            allPersons = groupManager.listPersonsByGroupId(tenantId, orgId).getData();
        } else if (orgType.equals(OrgTypeEnum.POSITION.getEnName())) {
            allPersons = positionManager.listPersonsByPositionId(tenantId, orgId).getData();
        } else if (orgType.equals(OrgTypeEnum.PERSON.getEnName())) {
            allPersons.add(personManager.get(tenantId, orgId).getData());
        }
        for (Person p : allPersons) {
            ids.add(p.getId());
        }
        return y9logAccessLogCustomRepository.pageByOrgType(tenantId, ids, operateType, page, rows);
    }

    @Override
    public Page<Y9logAccessLog> pageByTenantIdAndManagerLevelAndUserId(String tenantId, String managerLevel,
        String userId, Integer page, Integer rows, String sort) {
        return y9logAccessLogCustomRepository.pageByTenantIdAndManagerLevelAndUserId(tenantId, managerLevel, userId,
            page, rows, sort);
    }

    @Override
    public Page<Y9logAccessLog> pageElapsedTimeByCondition(LogInfoModel searchDto, String startDay, String endDay,
        String sTime, String lTime, Integer page, Integer rows) throws ParseException {
        return y9logAccessLogCustomRepository.pageElapsedTimeByCondition(searchDto, startDay, endDay, sTime, lTime,
            getTenantType(Y9LoginUserHolder.getTenantId()), page, rows);
    }

    @Override
    public Page<Y9logAccessLog> pageOperateStatusByOperateStatus(LogInfoModel searchDto, String operateStatus,
        String date, String hour, Integer page, Integer rows) throws ParseException {

        return y9logAccessLogCustomRepository.pageOperateStatusByOperateStatus(searchDto, operateStatus, date, hour,
            getTenantType(Y9LoginUserHolder.getTenantId()), page, rows);
    }

    @Override
    public Page<Y9logAccessLog> pageSearchByCondition(LogInfoModel searchDto, String startTime, String endTime,
        Integer page, Integer rows) {
        return y9logAccessLogCustomRepository.pageSearchByCondition(searchDto, startTime, endTime,
            getTenantType(Y9LoginUserHolder.getTenantId()), page, rows);
    }

    @Override
    public void save(Y9logAccessLog y9logAccessLog) {
        if(StringUtils.isBlank(y9logAccessLog.getManagerLevel())){
            y9logAccessLog.setManagerLevel("0");
        }
        y9logAccessLogCustomRepository.save(y9logAccessLog);
    }

    @Override
    public Page<Y9logAccessLog> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
        Integer page, Integer rows) {
        return y9logAccessLogCustomRepository.searchQuery(tenantId, managerLevel, loginInfoModel, page, rows);
    }
}
