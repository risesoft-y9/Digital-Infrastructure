package net.risesoft.repository.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.log.constant.Y9LogSearchConsts;
import net.risesoft.log.domain.Y9LogUserLoginInfoDO;
import net.risesoft.log.repository.Y9logUserLoginInfoCustomRepository;
import net.risesoft.model.log.LogInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.Y9LogUserLoginInfo;
import net.risesoft.y9public.repository.Y9LogUserLoginInfoRepository;

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

    private static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final Y9LogUserLoginInfoRepository y9logUserLoginInfoRepository;

    @Override
    public long countByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success) {
        return y9logUserLoginInfoRepository.count(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogUserLoginInfo> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                if (StringUtils.isNotBlank(success)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                }
                String tenantId = Y9LoginUserHolder.getTenantId();
                if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                list.add(
                    criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class), startTime, endTime));
                return predicate;
            }
        });
    }

    @Override
    public long countBySuccessAndUserHostIpAndUserId(String success, String userHostIp, String userId) {
        return y9logUserLoginInfoRepository.count(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogUserLoginInfo> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
                String tenantId = Y9LoginUserHolder.getTenantId();
                if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                if (StringUtils.isNotBlank(userHostIp)) {
                    list.add(
                        criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class), userHostIp));
                }
                if (StringUtils.isNotBlank(userId)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_ID).as(String.class), userId));
                }
                if (StringUtils.isNotBlank(success)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                }
                return predicate;
            }
        });
    }

    @Override
    public long countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp, Date startTime, Date endTime,
        String success) {
        return y9logUserLoginInfoRepository.count(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogUserLoginInfo> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
                String tenantId = Y9LoginUserHolder.getTenantId();
                if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                if (StringUtils.isNotBlank(success)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                }
                if (StringUtils.isNotBlank(userHostIp)) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                        userHostIp + "%"));
                }
                list.add(
                    criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class), startTime, endTime));
                return predicate;
            }
        });
    }

    @Override
    public List<Map<String, Object>> listUserHostIpByCip(String cip) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> userHostIpList;
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            userHostIpList = y9logUserLoginInfoRepository.findDistinctUserHostIpCountByUserHostIpLike(cip);
        } else {
            userHostIpList =
                y9logUserLoginInfoRepository.findDistinctUserHostIpCountByUserHostIpLikeAndTenantId(cip, tenantId);
        }
        if (!userHostIpList.isEmpty()) {
            userHostIpList.forEach(userHostIp -> {
                Map<String, Object> map = new HashMap<>();
                String text =
                    userHostIp.get("userHostIp") + "<span style='color:red'>(" + userHostIp.get("count") + ")</span>";
                map.put("serverIp", userHostIp.get("userHostIp"));
                map.put("text", text);
                list.add(map);
            });

        }
        return list;
    }

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> page(String tenantId, String userHostIp, String userId, String success,
        String startTime, String endTime, Y9PageQuery pageQuery) {
        int page = pageQuery.getPage4Db();
        int size = pageQuery.getSize();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Page<Y9LogUserLoginInfo> pageInfo = y9logUserLoginInfoRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogUserLoginInfo> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                if (StringUtils.isNotBlank(userId)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.USER_ID).as(String.class), userId));
                }
                if (StringUtils.isNotBlank(tenantId)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                if (StringUtils.isNotBlank(success)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                }
                if (StringUtils.isNotBlank(userHostIp)) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                        "%" + userHostIp + "%"));
                }
                if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                    try {
                        list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class),
                            DATE_TIME_FORMAT.parse(startTime), DATE_TIME_FORMAT.parse(endTime)));
                    } catch (ParseException e) {
                        LOGGER.warn(e.getMessage(), e);
                    }
                }

                return predicate;
            }
        }, pageable);
        List<Y9LogUserLoginInfoDO> list = pageInfo.getContent()
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(), list);
    }

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> pageByLoginTimeBetweenAndSuccess(Date startTime, Date endTime, String success,
        int page, int rows) {
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));

        Page<Y9LogUserLoginInfo> pageInfo = y9logUserLoginInfoRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogUserLoginInfo> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                if (StringUtils.isNotBlank(success)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                }
                if (startTime != null && endTime != null) {
                    list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class), startTime,
                        endTime));
                }
                String tenantId = Y9LoginUserHolder.getTenantId();
                if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                return predicate;
            }
        }, pageable);
        List<Y9LogUserLoginInfoDO> list = pageInfo.getContent()
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(), list);
    }

    @Override
    public Y9Page<Map<String, Object>> pageByUserHostIpAndSuccess(String userHostIp, String success, int page,
        int size) {
        List<Map<String, Object>> strList = new ArrayList<>();
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, size);
        Page<Object[]> pageInfo;
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            pageInfo = y9logUserLoginInfoRepository.findDistinctByUserHostIpAndSuccess(userHostIp, success, pageable);
        } else {
            pageInfo = y9logUserLoginInfoRepository.findDistinctByUserHostIpAndSuccessAndTenantId(userHostIp, success,
                tenantId, pageable);
        }
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
        Pageable pageable = PageRequest.of((page < 1) ? 0 : page - 1, size);
        Page<Object[]> pageInfo;
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
            pageInfo = y9logUserLoginInfoRepository.findByUserHostIpAndSuccessAndUserNameLike(userHostIp, success,
                userName, pageable);
        } else {
            pageInfo = y9logUserLoginInfoRepository.findByUserHostIpAndSuccessAndUserNameLikeAndTenantId(userHostIp,
                success, userName, tenantId, pageable);
        }
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

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> pageByUserHostIpLikeAndLoginTimeBetweenAndSuccess(String userHostIp,
        Date startTime, Date endTime, String success, int page, int rows) {

        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));
        Page<Y9LogUserLoginInfo> pageInfo = y9logUserLoginInfoRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogUserLoginInfo> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();
                if (StringUtils.isNotBlank(success)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.SUCCESS).as(String.class), success));
                }
                if (StringUtils.isNotBlank(userHostIp)) {
                    list.add(criteriaBuilder.like(root.get(Y9LogSearchConsts.USER_HOST_IP).as(String.class),
                        userHostIp + "%"));
                }
                if (startTime != null && endTime != null) {
                    list.add(criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class), startTime,
                        endTime));
                }
                String tenantId = Y9LoginUserHolder.getTenantId();
                if (!tenantId.equals(InitDataConsts.OPERATION_TENANT_ID)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
                }
                return predicate;
            }
        }, pageable);
        List<Y9LogUserLoginInfoDO> list = pageInfo.getContent()
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(), list);
    }

    public void save(Y9LogUserLoginInfoDO y9LogUserLoginInfoDO) {
        Y9LogUserLoginInfo y9LogUserLoginInfo =
            Y9ModelConvertUtil.convert(y9LogUserLoginInfoDO, Y9LogUserLoginInfo.class);
        y9logUserLoginInfoRepository.save(y9LogUserLoginInfo);
    }

    @Override
    public Y9Page<Y9LogUserLoginInfoDO> searchQuery(String tenantId, String managerLevel, LogInfoModel loginInfoModel,
        int page, int rows) {
        Pageable pageable =
            PageRequest.of((page < 1) ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, Y9LogSearchConsts.LOGIN_TIME));

        Page<Y9LogUserLoginInfo> pageInfo = y9logUserLoginInfoRepository.findAll(new Specification<>() {
            private static final long serialVersionUID = -2210269486911993525L;

            @Override
            public Predicate toPredicate(Root<Y9LogUserLoginInfo> root, CriteriaQuery<?> query,
                CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> list = predicate.getExpressions();

                if (StringUtils.isNotBlank(tenantId)) {
                    list.add(criteriaBuilder.equal(root.get(Y9LogSearchConsts.TENANT_ID).as(String.class), tenantId));
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
                    SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    try {
                        Date startDate = DATE_TIME_FORMAT.parse(sTime);
                        Date endDate = DATE_TIME_FORMAT.parse(eTime);
                        Date start = sdfUtc.parse(sdfUtc.format(startDate));
                        Date end = sdfUtc.parse(sdfUtc.format(endDate));
                        list.add(
                            criteriaBuilder.between(root.get(Y9LogSearchConsts.LOGIN_TIME).as(Date.class), start, end));
                    } catch (Exception e) {
                        LOGGER.warn(e.getMessage(), e);
                    }
                }
                return predicate;
            }
        }, pageable);
        List<Y9LogUserLoginInfoDO> list = pageInfo.getContent()
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
        return Y9Page.success(page, pageInfo.getTotalPages(), pageInfo.getTotalElements(), list);
    }

    @Override
    public Integer countByUserId(String personId) {
        return y9logUserLoginInfoRepository.countByUserId(personId);
    }

    @Override
    public Y9LogUserLoginInfoDO findTopByTenantIdAndUserIdOrderByLoginTimeDesc(String tenantId, String userId) {
        Y9LogUserLoginInfo y9LogUserLoginInfo =
            y9logUserLoginInfoRepository.findTopByTenantIdAndUserIdOrderByLoginTimeDesc(tenantId, userId);
        return Y9ModelConvertUtil.convert(y9LogUserLoginInfo, Y9LogUserLoginInfoDO.class);
    }

    @Override
    public List<Y9LogUserLoginInfoDO> findAll() {
        return y9logUserLoginInfoRepository.findAll()
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
    }

    @Override
    public Set<Y9LogUserLoginInfoDO> findByUserIdAndSuccess(String userId, String success) {
        return y9logUserLoginInfoRepository.findByUserIdAndSuccess(userId, success)
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toSet());
    }

    @Override
    public Page<Y9LogUserLoginInfoDO> findByTenantIdAndSuccessAndUserHostIpAndUserId(String tenantId, String success,
        String userHostIp, String userId, Pageable pageable) {
        Page<Y9LogUserLoginInfo> page = y9logUserLoginInfoRepository
            .findByTenantIdAndSuccessAndUserHostIpAndUserId(tenantId, success, userHostIp, userId, pageable);
        return poPageToDoPage(page);
    }

    @Override
    public Page<Y9LogUserLoginInfoDO> findBySuccessAndUserHostIpAndUserId(String success, String userHostIp,
        String userId, Pageable pageable) {
        Page<Y9LogUserLoginInfo> page =
            y9logUserLoginInfoRepository.findBySuccessAndUserHostIpAndUserId(success, userHostIp, userId, pageable);
        return poPageToDoPage(page);
    }

    @Override
    public Page<Y9LogUserLoginInfoDO> findByTenantIdAndManagerLevel(String tenantId, String managerLevel,
        Pageable pageable) {
        Page<Y9LogUserLoginInfo> page =
            y9logUserLoginInfoRepository.findByTenantIdAndManagerLevel(tenantId, managerLevel, pageable);
        return poPageToDoPage(page);
    }

    private static PageImpl<Y9LogUserLoginInfoDO> poPageToDoPage(Page<Y9LogUserLoginInfo> userLoginInfoPage) {
        List<Y9LogUserLoginInfoDO> list = userLoginInfoPage.getContent()
            .stream()
            .map(l -> Y9ModelConvertUtil.convert(l, Y9LogUserLoginInfoDO.class))
            .collect(Collectors.toList());
        return new PageImpl<>(list, userLoginInfoPage.getPageable(), userLoginInfoPage.getTotalElements());
    }
}
