package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.service.Y9logUserLoginInfoService;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9logUserLoginInfo;
import net.risesoft.y9public.repository.Y9logUserLoginInfoRepository;
import net.risesoft.y9public.repository.custom.Y9logUserLoginInfoCustomRepository;

/**
 *
 * @author guoweijun
 * @author shidaobang
 * @author mengjuhua
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Y9logUserLoginInfoServiceImpl implements Y9logUserLoginInfoService {

    private final Y9logUserLoginInfoRepository y9logUserLoginInfoRepository;
    private final Y9logUserLoginInfoCustomRepository y9logUserLoginInfoCustomRepository;

    @Override
    public long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success) {
        return y9logUserLoginInfoCustomRepository.countByLoginTimeBetweenAndSuccess(startTime, endTime, success);
    }

    @Override
    public Integer countByPersonId(String personId) {
        String parserPersonId = Y9Util.escape(personId);
        List<Y9logUserLoginInfo> list = y9logUserLoginInfoRepository.findByUserId(parserPersonId);
        return list.size();
    }

    @Override
    public long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId) {
        return y9logUserLoginInfoCustomRepository.countBySuccessAndUserHostIpAndUserId(success, userHostIp, userId);
    }

    @Override
    public long countByUserHostIpAndSuccess(String userHostIp, String success) {
        return y9logUserLoginInfoCustomRepository.countByUserHostIpAndSuccess(userHostIp, success);
    }

    @Override
    public long countByUserHostIpAndSuccessAndUserName(String userHostIp, String success, String userName) {
        return y9logUserLoginInfoRepository.countByUserHostIpAndSuccessAndUserName(userHostIp, success, userName);
    }

    @Override
    public long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime,
        String success) {

        return y9logUserLoginInfoCustomRepository.countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(userHostIp,
            startTime, endTime, success);
    }

    @Override
    public Y9logUserLoginInfo getTopByTenantIdAndUserId(String tenantId, String userId) {
        return y9logUserLoginInfoRepository.findTopByTenantIdAndUserIdOrderByLoginTimeDesc(tenantId, userId);
    }

    @Override
    public Iterable<Y9logUserLoginInfo> listAll() {
        return y9logUserLoginInfoRepository.findAll();
    }

    @Override
    public List<Object[]> listDistinctUserHostIpByUserIdAndLoginTime(String userId, Date startTime, Date endTime) {

        return y9logUserLoginInfoCustomRepository.listDistinctUserHostIpByUserIdAndLoginTime(userId, startTime,
            endTime);
    }

    @Override
    public List<Map<String, Object>> listUserHostIpByCip(String cip) {

        return y9logUserLoginInfoCustomRepository.listUserHostIpByCip(cip);
    }

    @Override
    public List<String> listUserHostIpByUserId(String userId, String success) {
        String parserUserId = Y9Util.escape(userId);
        Set<Y9logUserLoginInfo> list = y9logUserLoginInfoRepository.findByUserIdAndSuccess(parserUserId, success);
        Set<String> userHostIpSet = list.stream().map(Y9logUserLoginInfo::getUserHostIp).collect(Collectors.toSet());
        List<String> userHostIpList = new ArrayList<>();
        userHostIpList.addAll(userHostIpSet);
        return userHostIpList;
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> page(String tenantId, String userHostIp, String userId, String success,
        String startTime, String endTime, Y9PageQuery pageQuery) {
        return y9logUserLoginInfoCustomRepository.page(tenantId, userHostIp, userId, success, startTime, endTime,
            pageQuery);
    }

    @Override
    public Page<Y9logUserLoginInfo> pageBySuccessAndServerIpAndUserName(String success, String userHostIp,
        String userId, int page, int rows) {
        String parserUserId = Y9Util.escape(userId);
        String parserUserHostIp = Y9Util.escape(userHostIp);
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        return y9logUserLoginInfoRepository.findBySuccessAndUserHostIpAndUserId(success, parserUserHostIp, parserUserId,
            pageable);
    }

    @Override
    public Page<Y9logUserLoginInfo> pageByTenantIdAndManagerLevel(String tenantId, String managerLevel, int page,
        int rows) {
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        return y9logUserLoginInfoRepository.findByTenantIdAndManagerLevel(tenantId, managerLevel, pageable);
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
    public void save(Y9logUserLoginInfo y9logUserLoginInfo) {
        y9logUserLoginInfoRepository.save(y9logUserLoginInfo);
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> search(Date startTime, Date endTime, String success, int page, int rows) {

        return y9logUserLoginInfoCustomRepository.search(startTime, endTime, success, page, rows);
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> search(String userHostIp, Date startTime, Date endTime, String success, int page,
        int rows) {

        return y9logUserLoginInfoCustomRepository.search(userHostIp, startTime, endTime, success, page, rows);
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
        int page, int rows) {

        return y9logUserLoginInfoCustomRepository.searchQuery(tenantId, managerLevel, loginInfoModel, page, rows);
    }
}
