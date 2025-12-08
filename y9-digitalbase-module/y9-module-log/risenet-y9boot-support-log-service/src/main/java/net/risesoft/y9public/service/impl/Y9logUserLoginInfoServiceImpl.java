package net.risesoft.y9public.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.domain.Y9LogUserLoginInfoDO;
import net.risesoft.log.repository.Y9logUserLoginInfoCustomRepository;
import net.risesoft.model.log.LoginLogQuery;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.service.Y9logUserLoginInfoService;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Y9logUserLoginInfoServiceImpl implements Y9logUserLoginInfoService {

    private final Y9logUserLoginInfoCustomRepository y9logUserLoginInfoCustomRepository;

    @Override
    public long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success) {
        return y9logUserLoginInfoCustomRepository.countByLoginTimeBetweenAndSuccess(startTime, endTime, success);
    }

    @Override
    public Integer countByPersonId(String personId) {
        String parserPersonId = Y9Util.escape(personId);
        return y9logUserLoginInfoCustomRepository.countByUserId(parserPersonId);
    }

    @Override
    public long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId) {
        return y9logUserLoginInfoCustomRepository.countBySuccessAndUserHostIpAndUserId(success, userHostIp, userId);
    }

    @Override
    public long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime,
        String success) {

        return y9logUserLoginInfoCustomRepository.countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(userHostIp,
            startTime, endTime, success);
    }

    @Override
    public Y9LogUserLoginInfoDO getTopByTenantIdAndUserId(String tenantId, String userId) {
        return y9logUserLoginInfoCustomRepository.findTopByTenantIdAndUserIdOrderByLoginTimeDesc(tenantId, userId);
    }

    @Override
    public List<Y9LogUserLoginInfoDO> listAll() {
        return y9logUserLoginInfoCustomRepository.findAll();
    }

    @Override
    public List<Map<String, Object>> listUserHostIpByCip(String cip) {
        return y9logUserLoginInfoCustomRepository.listUserHostIpByCip(cip);
    }

    @Override
    public List<String> listUserHostIpByUserId(String userId, String success) {
        String parserUserId = Y9Util.escape(userId);
        Set<Y9LogUserLoginInfoDO> list =
            y9logUserLoginInfoCustomRepository.findByUserIdAndSuccess(parserUserId, success);
        Set<String> userHostIpSet = list.stream().map(Y9LogUserLoginInfoDO::getUserHostIp).collect(Collectors.toSet());
        List<String> userHostIpList = new ArrayList<>();
        userHostIpList.addAll(userHostIpSet);
        return userHostIpList;
    }

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> page(String tenantId, String userHostIp, String userId, String success,
        String startTime, String endTime, Y9PageQuery pageQuery) {
        return y9logUserLoginInfoCustomRepository.page(tenantId, userHostIp, userId, success, startTime, endTime,
            pageQuery);
    }

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> pageByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success,
        int page, int rows) {

        return y9logUserLoginInfoCustomRepository.pageByLoginTimeBetweenAndSuccess(startTime, endTime, success, page,
            rows);
    }

    @Override
    public Page<Y9LogUserLoginInfoDO> pageBySuccessAndUserHostIpAndUserId(String success, String userHostIp,
        String userId, int page, int rows) {
        String parserUserId = Y9Util.escape(userId);
        String parserUserHostIp = Y9Util.escape(userHostIp);
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            return y9logUserLoginInfoCustomRepository.findByTenantIdAndSuccessAndUserHostIpAndUserId(tenantId, success,
                parserUserHostIp, parserUserId, pageable);
        }
        return y9logUserLoginInfoCustomRepository.findBySuccessAndUserHostIpAndUserId(success, parserUserHostIp,
            parserUserId, pageable);
    }

    @Override
    public Page<Y9LogUserLoginInfoDO> pageByTenantIdAndManagerLevel(String tenantId, String managerLevel, int page,
        int rows) {
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        return y9logUserLoginInfoCustomRepository.findByTenantIdAndManagerLevel(tenantId, managerLevel, pageable);
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccess(String userHostIp, String success, int page,
        int rows) {
        return y9logUserLoginInfoCustomRepository.pageByUserHostIpAndSuccess(userHostIp, success, page, rows);
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success,
        String userName, int page, int rows) {

        return y9logUserLoginInfoCustomRepository.pageByUserHostIpAndSuccessAndUserNameLike(userHostIp, success,
            userName, page, rows);
    }

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> pageByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp,
        Date startTime, Date endTime, String success, int page, int rows) {

        return y9logUserLoginInfoCustomRepository.pageByUserHostIpLikeAndLoginTimeBetweenAndSuccess(userHostIp,
            startTime, endTime, success, page, rows);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Y9LogUserLoginInfoDO y9LogUserLoginInfoDO) {
        if (StringUtils.isBlank(y9LogUserLoginInfoDO.getManagerLevel())) {
            y9LogUserLoginInfoDO.setManagerLevel("0");
        }
        y9logUserLoginInfoCustomRepository.save(y9LogUserLoginInfoDO);
    }

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> searchQuery(String tenantId, String managerLevel, LoginLogQuery loginInfoModel,
        int page, int rows) {
        return y9logUserLoginInfoCustomRepository.searchQuery(tenantId, managerLevel, loginInfoModel, page, rows);
    }
}
