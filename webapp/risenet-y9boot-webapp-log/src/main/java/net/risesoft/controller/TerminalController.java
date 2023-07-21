package net.risesoft.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.annotation.RiseLog;
import net.risesoft.log.entity.Y9logIpDeptMapping;
import net.risesoft.log.entity.Y9logUserLoginInfo;
import net.risesoft.log.service.Y9logIpDeptMappingService;
import net.risesoft.log.service.Y9logUserHostIpInfoService;
import net.risesoft.log.service.Y9logUserLoginInfoService;
import net.risesoft.model.Person;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Day;

import y9.client.platform.org.PersonApiClient;

/**
 * 终端管理
 *
 * @author mengjuhua
 * @author guoweijun
 * @author shidaobang
 */
@RestController
@RequestMapping("/admin/terminal")
@Slf4j
@RequiredArgsConstructor
public class TerminalController {

    private final PersonApiClient personManager;
    private final Y9logIpDeptMappingService y9logIpDeptMappingService;
    private final Y9logUserLoginInfoService y9logUserLoginInfoService;
    private final Y9logUserHostIpInfoService y9logUserHostIpInfoService;

    /**
     * 获取地址段的登录人数数据
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @RiseLog(operationName = "获取地址段的登录人数数据")
    @GetMapping(value = "/getUserLoginDataByIpSection")
    public Y9Result<Map<String, Object>> getUserLoginDataByIpSection(String startTime, String endTime) {
        Map<String, Object> map = new HashMap<>();
        List<String> clientIpList = y9logIpDeptMappingService.listClientIpSections();
        List<Long> countList = new ArrayList<>();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (!clientIpList.isEmpty()) {
                for (String clientIp : clientIpList) {
                    long count = y9logUserLoginInfoService.countByUserHostIpLikeAndLoginTimeBetweenAndSuccess(clientIp, formater.parse(startTime), formater.parse(endTime), "true");
                    countList.add(count);
                }
            }
            long personCount = y9logUserLoginInfoService.countByLoginTimeBetweenAndSuccess(formater.parse(startTime), formater.parse(endTime), "true");
            map.put("personCount", personCount);
        } catch (ParseException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        map.put("clientIpList", clientIpList);
        map.put("countList", countList);
        return Y9Result.success(map, "获取数据成功");
    }

    /**
     * 获取所有登录的终端IP
     *
     * @return
     */
    @RiseLog(operationName = "获取所有登录的终端IP")
    @GetMapping(value = "/listAllUserHostIPs")
    public Y9Result<List<Map<String, Object>>> listAllUserHostIPs() {
        List<Map<String, Object>> cIPlist = new ArrayList<>();
        List<Y9logIpDeptMapping> ipDeptMappingList = y9logIpDeptMappingService.listAllOrderByClientIpSection();
        for (Y9logIpDeptMapping ipDeptMapping : ipDeptMappingList) {
            String cip = ipDeptMapping.getClientIpSection();
            HashMap<String, Object> cIPMap = new HashMap<>();
            cIPMap.put("pid", 0);
            cIPMap.put("name", cip + "(" + ipDeptMapping.getDeptName() + ")");
            cIPMap.put("CIP", cip);
            cIPlist.add(cIPMap);
        }
        return Y9Result.success(cIPlist, "获取数据成功");
    }

    /**
     * 根据C类IP段，获取属于该IP段的IP地址和次数
     *
     * @param cip c类IP段
     * @return String
     */
    @RiseLog(operationName = "根据C类IP段，获取属于该IP段的IP地址和次数")
    @GetMapping(value = "/listTerminalIpByCip")
    public Y9Result<List<Map<String, Object>>> listTerminalIpByCip(String cip) {
        List<Map<String, Object>> list = y9logUserLoginInfoService.listUserHostIpByCip(cip);
        return Y9Result.success(list);
    }

    /**
     * 查询出该时间段终端IP的登录详情页面
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userHostIp 终端IP
     * @return
     */
    @RiseLog(operationName = "查询出该时间段终端IP的登录详情页面")
    @GetMapping(value = "/listTerminator")
    public Y9Result<List<String>> listTerminator(String startTime, String endTime, String userHostIp) {
        List<String> userHostIpList = null;
        if (StringUtils.isNotBlank(userHostIp)) {
            userHostIpList = y9logUserHostIpInfoService.listUserHostIpByUserHostIpLike(userHostIp);
        } else {
            userHostIpList = y9logUserHostIpInfoService.listAllUserHostIps();
        }
        return Y9Result.success(userHostIpList);
    }

    /**
     * 根据人员id，获取所有登陆成功的终端ip
     *
     * @param userId 人员id
     * @return
     */
    @RiseLog(operationName = "根据人员id，获取所有登陆成功的终端ip")
    @GetMapping(value = "/listUserHostIpByUserId")
    public Y9Result<List<Map<String, Object>>> listUserHostIpByUserId(@RequestParam String userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<String> userHostIPList = y9logUserLoginInfoService.listUserHostIpByUserId(userId, "true");
        for (String userHostIP : userHostIPList) {
            // 根据userId和userHostIP查询人员登录成功的次数
            long counter = y9logUserLoginInfoService.countBySuccessAndUserHostIpAndUserId("true", userHostIP, userId);
            HashMap<String, Object> map = new HashMap<>();
            map.put("userHostIp", userHostIP);
            map.put("name", userHostIP + "(" + counter + ")");
            list.add(map);
        }
        return Y9Result.success(list);
    }

    /**
     * 根据部门获取部门下的所有人员
     *
     * @param parentId 父节点id
     * @param pageQuery 分页详情
     * @return
     */
    @RiseLog(operationName = "根据部门获取部门下的所有人员")
    @GetMapping(value = "/pagePersonByDeptId")
    public Y9Page<Map<String, Object>> pagePersonByDeptId(String parentId, Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> list = new ArrayList<>();
        Y9Page<Person> personPage = personManager.pageByParentId(tenantId, parentId, false, pageQuery.getPage(), pageQuery.getSize());
        List<Person> personList = Y9JsonUtil.readList(Y9JsonUtil.writeValueAsString(personPage.getRows()), Person.class);
        if (!personList.isEmpty()) {
            personList.forEach(person -> {
                Map<String, Object> map = new HashMap<>();
                Integer countNum = y9logUserLoginInfoService.countByPersonId(person.getId());
                if (countNum > 0) {
                    map.put("id", person.getId());
                    map.put("loginName", person.getName());
                    map.put("dn", person.getDn());
                    map.put("loginNum", countNum);
                    list.add(map);
                }
            });
        }
        return Y9Page.success(pageQuery.getPage(), 0, list.size(), list);
    }

    /**
     * 根据部门id以及人员名称模糊查询该部门下的人员
     *
     * @param parentId 父节点id
     * @param userName 用户名称
     * @param pageQuery 分页详情
     * @return
     */
    @RiseLog(operationName = "根据部门id以及人员名称模糊查询该部门下的人员")
    @GetMapping(value = "/pagePersonByDeptIdAndUserName")
    public Y9Page<Map<String, Object>> pagePersonByDeptIdAndUserName(String parentId, String userName, Y9PageQuery pageQuery) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> list = new ArrayList<>();
        Y9Page<Person> personPage = personManager.pageByParentIdAndUserName(tenantId, parentId, false, userName, pageQuery.getPage(), pageQuery.getSize());
        List<Person> personList = Y9JsonUtil.readList(Y9JsonUtil.writeValueAsString(personPage.getRows()), Person.class);
        if (!personList.isEmpty()) {
            for (Person orgPerson : personList) {
                Integer countNum = y9logUserLoginInfoService.countByPersonId(orgPerson.getId());
                if (countNum > 0) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", orgPerson.getId());
                    map.put("loginName", orgPerson.getName());
                    map.put("dn", orgPerson.getDn());
                    map.put("loginNum", countNum.toString());
                    list.add(map);
                }
            }
        }
        return Y9Page.success(pageQuery.getPage(), 0, list.size(), list);
    }

    /**
     * 根据终端C段IP和时间段查询出该时间段终端IP的登录详情
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userHostIp 终端IP
     * @param pageQuery 分页详情
     * @return
     */
    @RiseLog(operationName = "根据终端C段IP和时间段查询出该时间段终端IP的登录详情")
    @GetMapping(value = "/pageSearchByLoginTime")
    public Y9Page<Y9logUserLoginInfo> pageSearchByLoginTime(String startTime, String endTime, String userHostIp, Y9PageQuery pageQuery) {
        Date start = null;
        Date end = null;
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (StringUtils.isNotBlank(startTime)) {
                start = formater.parse(startTime);
            } else {
                start = Y9Day.getStartOfDay(new Date());
            }
            if (StringUtils.isNotBlank(endTime)) {
                end = formater.parse(endTime);
            } else {
                end = Y9Day.getEndOfDay(new Date());
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Y9Page<Y9logUserLoginInfo> userLoginInfoList = null;
        if (StringUtils.isNotBlank(userHostIp)) {
            userLoginInfoList = y9logUserLoginInfoService.search(userHostIp, start, end, "true", pageQuery.getPage(), pageQuery.getSize());
        } else {
            userLoginInfoList = y9logUserLoginInfoService.search(start, end, "true", pageQuery.getPage(), pageQuery.getSize());
        }

        return userLoginInfoList;
    }

    /**
     * 根据终端IP和人员以及时间段模糊搜索改人员的详细信息
     *
     * @param userHostIp 终端IP
     * @param userId 用户id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageQuery 分页详情
     * @return
     */
    @RiseLog(operationName = "根据终端IP和人员以及时间段模糊搜索改人员的详细信息")
    @GetMapping(value = "/pageSearchList")
    public Y9Page<Y9logUserLoginInfo> pageSearchList(String userHostIp, String userId, String startTime, String endTime, Y9PageQuery pageQuery) {
        return y9logUserLoginInfoService.page(userHostIp, userId, "true", startTime, endTime, pageQuery.getPage(), pageQuery.getSize());
    }

    /**
     * 根据不同的终端IP，查询相关人员信息
     *
     * @param userHostIp 终端IP
     * @param userName 用户名称
     * @param pageQuery 分页详情
     * @return
     */
    @RiseLog(operationName = "根据不同的终端IP，查询相关人员信息")
    @GetMapping(value = "/pageUserLoginListData")
    public Y9Page<Map<String, Object>> pageUserLoginListData(String userHostIp, String userName, Y9PageQuery pageQuery) {
        if (StringUtils.isNotBlank(userName)) {
            return y9logUserLoginInfoService.pageByUserHostIpAndSuccessAndUserNameLike(userHostIp, "true", userName, pageQuery.getPage(), pageQuery.getSize());
        }
        return y9logUserLoginInfoService.pageByUserHostIpAndSuccess(userHostIp, "true", pageQuery.getPage(), pageQuery.getSize());
    }

    /**
     * 根据终端IP和人员获取人员的详细信息
     *
     * @param userId 用户id
     * @param userHostIp 终端IP
     * @param pageQuery 分页详情
     * @return
     */
    @RiseLog(operationName = "根据终端IP和人员获取人员的详细信息")
    @GetMapping(value = "/pageUserTerminalDetail")
    public Y9Page<Y9logUserLoginInfo> pageUserTerminalDetail(String userId, String userHostIp, Y9PageQuery pageQuery) {
        Page<Y9logUserLoginInfo> userLoginInfoList = y9logUserLoginInfoService.pageBySuccessAndServerIpAndUserName("true", userHostIp, userId, pageQuery.getPage(), pageQuery.getSize());
        return Y9Page.success(pageQuery.getPage(), userLoginInfoList.getTotalPages(), userLoginInfoList.getTotalElements(), userLoginInfoList.getContent());
    }
}