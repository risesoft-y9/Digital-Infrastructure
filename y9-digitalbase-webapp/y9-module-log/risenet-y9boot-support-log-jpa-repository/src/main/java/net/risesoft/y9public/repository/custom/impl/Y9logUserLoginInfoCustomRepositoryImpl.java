package net.risesoft.y9public.repository.custom.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class Y9logUserLoginInfoCustomRepositoryImpl implements Y9logUserLoginInfoCustomRepository {

    private final Y9logUserLoginInfoRepository y9logUserLoginInfoRepository;

    @Override
    public long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success) {
        List<Y9logUserLoginInfo> ipList = y9logUserLoginInfoRepository.findAll(new Specification<Y9logUserLoginInfo>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logUserLoginInfo> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                if (StringUtils.isNotBlank(success)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                }
                list.add(
                    criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class), startTime, endTime));
                return predicate;
            }
        });

        return ipList.size();
    }

    @Override
    public long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId) {
        List<Y9logUserLoginInfo> ipList = y9logUserLoginInfoRepository.findAll(new Specification<Y9logUserLoginInfo>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logUserLoginInfo> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                if (StringUtils.isNotBlank(success)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                }
                if (StringUtils.isNotBlank(userHostIp)) {
                    String parserUserHostIp = Y9Util.escape(userHostIp);
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                        parserUserHostIp));
                }
                if (StringUtils.isNotBlank(userId)) {
                    String parserUserId = Y9Util.escape(userId);
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_ID).as(String.class), parserUserId));
                }
                return predicate;
            }
        });

        return ipList.size();
    }

    @Override
    public long countByUserHostIpAndSuccess(String userHostIp, String success) {
        return y9logUserLoginInfoRepository.countByUserHostIpAndSuccess(userHostIp, success);
    }

    @Override
    public long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime,
        String success) {
        List<Y9logUserLoginInfo> ipList = y9logUserLoginInfoRepository.findAll(new Specification<Y9logUserLoginInfo>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logUserLoginInfo> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                if (StringUtils.isNotBlank(success)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                }
                if (StringUtils.isNotBlank(userHostIp)) {
                    String parserUserHostIp = Y9Util.escape(userHostIp);
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                        parserUserHostIp + "%"));
                }
                list.add(
                    criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class), startTime, endTime));
                return predicate;
            }
        });
        return ipList.size();
    }

    @Override
    public List<Object[]> listDistinctUserHostIpByUserIdAndLoginTime(String userId, Date startTime, Date endTime) {
        List<Object[]> strList = new ArrayList<>();
        List<Object[]> userHostIpList =
            y9logUserLoginInfoRepository.findDistinctUserHostIpByUserIdAndLoginTime(userId, startTime, endTime);
        if (!userHostIpList.isEmpty()) {
            userHostIpList.forEach(userHostIp -> {
                String[] userLoginInfo = {userHostIp[0] + "", String.valueOf(userHostIp[1])};
                strList.add(userLoginInfo);
            });
        }
        return strList;
    }

    @Override
    public List<Map<String, Object>> listUserHostIpByCip(String cip) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Object[]> userHostIpList = y9logUserLoginInfoRepository.findDistinctByUserHostIpLike(cip);
        if (!userHostIpList.isEmpty()) {
            userHostIpList.forEach(userHostIp -> {
                Map<String, Object> map = new HashMap<>();
                String text = userHostIp[0] + "<span style='color:red'>(" + userHostIp[1] + ")</span>";
                map.put("serverIp", userHostIp[0]);
                map.put("text", text);
                list.add(map);
            });

        }
        return list;
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> page(String tenantId, String userHostIp, String userId, String success,
        String startTime, String endTime, Y9PageQuery pageQuery) {
        String parserUserId = Y9Util.escape(userId);
        int page = pageQuery.getPage4Db();
        int size = pageQuery.getSize();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Page<Y9logUserLoginInfo> ipPage = y9logUserLoginInfoRepository.findAll(new Specification<Y9logUserLoginInfo>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9logUserLoginInfo> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                if (StringUtils.isNotBlank(parserUserId)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_ID).as(String.class), parserUserId));
                }
                if (StringUtils.isNotBlank(tenantId)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                if (StringUtils.isNotBlank(success)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                }
                if (StringUtils.isNotBlank(userHostIp)) {
                    String parserUserHostIp = Y9Util.escape(userHostIp);
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                        "%" + parserUserHostIp + "%"));
                }
                if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class),
                            simpleDateFormat.parse(startTime), simpleDateFormat.parse(endTime)));
                    } catch (ParseException e) {
                        LOGGER.warn(e.getMessage(), e);
                    }
                }

                return predicate;
            }
        }, pageable);

        return Y9Page.success(page, ipPage.getTotalPages(), ipPage.getTotalElements(), ipPage.getContent());
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccess(String userHostIp, String success, int page,
        int size) {
        List<Map<String, Object>> strList = new ArrayList<>();
        // 查询条件
        // 聚合
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, size, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Page<Object[]> pageInfo =
            y9logUserLoginInfoRepository.findDistinctByUserHostIpAndSuccess(userHostIp, success, pageable);
        if (!pageInfo.getContent().isEmpty()) {
            pageInfo.getContent().forEach(logUserLoginInfo -> {
                String userId = (String)logUserLoginInfo[0];
                String userName = (String)logUserLoginInfo[1];
                Map<String, Object> map = new HashMap<>();
                map.put(Y9LogSearchConsts.USER_ID, userId);
                map.put(Y9LogSearchConsts.USER_NAME, userName);
                map.put(Y9LogSearchConsts.USER_HOST_IP, logUserLoginInfo[2]);
                map.put("serverCount", String.valueOf(logUserLoginInfo[3]));
                strList.add(map);
            });
        }
        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(), strList);
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccessAndUserNameLike(String userHostIp, String success,
        String userName, int page, int size) {
        List<Map<String, Object>> strList = new ArrayList<>();
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, size, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Page<Object[]> pageInfo = y9logUserLoginInfoRepository.findByUserHostIpAndSuccessAndUserNameLike(userHostIp,
            success, userName, pageable);
        if (!pageInfo.getContent().isEmpty()) {
            pageInfo.getContent().forEach(logUserLoginInfo -> {
                String userId = (String)logUserLoginInfo[0];
                String name = (String)logUserLoginInfo[1];
                Map<String, Object> map = new HashMap<>();
                map.put(Y9LogSearchConsts.USER_ID, userId);
                map.put(Y9LogSearchConsts.USER_NAME, name);
                map.put(Y9LogSearchConsts.USER_HOST_IP, logUserLoginInfo[2]);
                map.put("serverCount", String.valueOf(logUserLoginInfo[3]));
                strList.add(map);
            });
        }
        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(), strList);
    }

    public void save(Y9logUserLoginInfo y9logUserLoginInfo) {
        y9logUserLoginInfoRepository.save(y9logUserLoginInfo);
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> search(Date startTime, Date endTime, String success, int page, int rows) {

        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));

        Page<Y9logUserLoginInfo> pageInfo =
            y9logUserLoginInfoRepository.findAll(new Specification<Y9logUserLoginInfo>() {
                private static final long serialVersionUID = -2210269486911993525L;

                @Override
                public Predicate toPredicate(Root<Y9logUserLoginInfo> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                    Predicate predicate = criteriaBuilder.conjunction();
                    List<Expression<Boolean>> list = predicate.getExpressions();

                    if (StringUtils.isNotBlank(success)) {
                        list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                    }
                    if (startTime != null && endTime != null) {
                        list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class),
                            startTime, endTime));
                    }

                    return predicate;
                }
            }, pageable);

        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(), pageInfo.getContent());
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> search(String userHostIp, Date startTime, Date endTime, String success, int page,
        int rows) {

        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Page<Y9logUserLoginInfo> pageInfo =
            y9logUserLoginInfoRepository.findAll(new Specification<Y9logUserLoginInfo>() {
                private static final long serialVersionUID = -2210269486911993525L;

                @Override
                public Predicate toPredicate(Root<Y9logUserLoginInfo> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                    Predicate predicate = criteriaBuilder.conjunction();
                    List<Expression<Boolean>> list = predicate.getExpressions();

                    if (StringUtils.isNotBlank(userHostIp)) {
                        String parserUserHostIp = Y9Util.escape(userHostIp);
                        list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                            "%" + parserUserHostIp + "%"));
                    }

                    if (StringUtils.isNotBlank(success)) {
                        list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                    }
                    if (startTime != null && endTime != null) {
                        list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class),
                            startTime, endTime));
                    }

                    return predicate;
                }
            }, pageable);

        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(), pageInfo.getContent());
    }

    @Override
    public Y9Page<Y9logUserLoginInfo> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
        int page, int rows) {
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));

        Page<Y9logUserLoginInfo> pageInfo =
            y9logUserLoginInfoRepository.findAll(new Specification<Y9logUserLoginInfo>() {
                private static final long serialVersionUID = -2210269486911993525L;

                @Override
                public Predicate toPredicate(Root<Y9logUserLoginInfo> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                    Predicate predicate = criteriaBuilder.conjunction();
                    List<Expression<Boolean>> list = predicate.getExpressions();

                    if (StringUtils.isNotBlank(tenantId)) {
                        list.add(
                            criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                    }
                    if (StringUtils.isNotBlank(managerLevel)) {
                        list.add(criteriaBuilder.equal(root.get("managerLevel").as(String.class), managerLevel));
                    }
                    if (StringUtils.isNotBlank(loginInfoModel.getUserName())) {
                        list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.USER_NAME).as(String.class),
                            "%" + loginInfoModel.getUserName() + "%"));
                    }
                    if (StringUtils.isNotBlank(loginInfoModel.getUserHostIp())) {
                        list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                            "%" + loginInfoModel.getUserHostIp() + "%"));
                    }
                    if (StringUtils.isNotBlank(loginInfoModel.getOsName())) {
                        list.add(criteriaBuilder.like(root.get("osName").as(String.class),
                            "%" + loginInfoModel.getOsName() + "%"));
                    }
                    if (StringUtils.isNotBlank(loginInfoModel.getScreenResolution())) {
                        list.add(criteriaBuilder.like(root.get("screenResolution").as(String.class),
                            "%" + loginInfoModel.getScreenResolution() + "%"));
                    }
                    if (StringUtils.isNotBlank(loginInfoModel.getSuccess())) {

                        list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class),
                            loginInfoModel.getSuccess()));
                    }
                    if (StringUtils.isNotBlank(loginInfoModel.getBrowserName())) {
                        list.add(criteriaBuilder.like(root.get("browserName").as(String.class),
                            "%" + loginInfoModel.getBrowserName() + "%"));
                    }
                    if (StringUtils.isNotBlank(loginInfoModel.getBrowserVersion())) {
                        list.add(criteriaBuilder.like(root.get("browserVersion").as(String.class),
                            "%" + loginInfoModel.getBrowserVersion() + "%"));
                    }
                    if (StringUtils.isNotBlank(loginInfoModel.getStartTime())
                        && StringUtils.isNotBlank(loginInfoModel.getEndTime())) {
                        String sTime = loginInfoModel.getStartTime() + " 00:00:00";
                        String eTime = loginInfoModel.getEndTime() + " 23:59:59";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        try {
                            Date startDate = sdf.parse(sTime);
                            Date endDate = sdf.parse(eTime);
                            Date start = sdfUtc.parse(sdfUtc.format(startDate));
                            Date end = sdfUtc.parse(sdfUtc.format(endDate));
                            list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class),
                                start, end));
                        } catch (Exception e) {
                            LOGGER.warn(e.getMessage(), e);
                        }
                    }
                    return predicate;
                }
            }, pageable);
        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(), pageInfo.getContent());
    }

}
