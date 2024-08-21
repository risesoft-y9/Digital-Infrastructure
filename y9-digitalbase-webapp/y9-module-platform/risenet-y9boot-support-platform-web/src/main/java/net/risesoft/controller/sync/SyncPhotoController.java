package net.risesoft.controller.sync;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Person;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9PersonExtService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.y9.Y9LoginUserHolder;

import jodd.util.Base64;

/**
 * 同步照片
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping("/syncPhoto")
@Slf4j
public class SyncPhotoController {

    private final JdbcTemplate jdbcTemplate4Tenant;
    private final JdbcTemplate jdbcTemplate;

    private final Y9PersonService y9PersonService;
    private final Y9PersonExtService y9PersonExtService;

    public SyncPhotoController(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant,
        @Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate, Y9PersonService y9PersonService,
        Y9PersonExtService y9PersonExtService) {
        this.jdbcTemplate4Tenant = jdbcTemplate4Tenant;
        this.jdbcTemplate = jdbcTemplate;
        this.y9PersonService = y9PersonService;
        this.y9PersonExtService = y9PersonExtService;
    }

    public String getPhotoById(@RequestParam String personId) {
        List<Blob> photoList =
            jdbcTemplate4Tenant.queryForList("select PHOTO from Y9_ORG_PERSON t where t.id=? ", Blob.class, personId);
        if (!photoList.isEmpty() && null != photoList.get(0)) {
            Blob p = photoList.get(0);
            try {
                return Base64.encodeToString(p.getBytes(1, (int)p.length()));
            } catch (SQLException e) {
                LOGGER.warn(e.getMessage(), e);
                return Base64.encodeToString(new byte[0]);
            }
        }
        return Base64.encodeToString(new byte[0]);
    }

    /**
     * 同步全部人员头像信息
     *
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/syncPersonPhoto")
    @RiseLog(operationName = "同步人员信息", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncPersonPhoto() {
        String sql = "select id from y9_common_tenant";
        List<String> tenantIdList = jdbcTemplate.queryForList(sql, String.class);
        for (String tenantId : tenantIdList) {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Y9Person> persons = y9PersonService.listAll();
            for (Y9Person person : persons) {
                y9PersonExtService.savePersonPhoto(person, getPhotoById(person.getId()));
            }
        }

        return Y9Result.successMsg("同步完成");
    }

    /**
     * 根据租户id，同步租户人员头像信息
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/syncTenantPersonPhoto")
    @RiseLog(operationName = "同步人员信息", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncPersonPhoto2(@RequestParam String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9Person> persons = y9PersonService.listAll();
        for (Y9Person person : persons) {
            y9PersonExtService.savePersonPhoto(person, getPhotoById(person.getId()));
        }

        return Y9Result.successMsg("同步完成");
    }

    /**
     * 同步个人头像信息
     *
     * @param tenantId 租户id
     * @param id 人员id
     * @return {@code Y9Result<String>}
     */
    @RequestMapping("/syncPersonPhotoByPersonId")
    @RiseLog(operationName = "同步人员信息", operationType = OperationTypeEnum.MODIFY)
    public Y9Result<String> syncPersonPhotoByPersonId(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Person person = y9PersonService.getById(id);
        if (person != null && person.getId() != null) {
            y9PersonExtService.savePersonPhoto(person, getPhotoById(person.getId()));
        }

        return Y9Result.successMsg("同步完成");
    }
}
