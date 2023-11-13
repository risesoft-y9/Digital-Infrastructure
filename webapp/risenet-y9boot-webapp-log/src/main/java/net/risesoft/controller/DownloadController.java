package net.risesoft.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.entity.Y9logUserLoginInfo;
import net.risesoft.log.service.Y9logUserLoginInfoService;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.LoginInformation;
import net.risesoft.pojo.PersonInformation;
import net.risesoft.util.JxlsUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.mime.ContentDispositionUtil;

import y9.client.platform.org.PersonApiClient;

@Controller
@RequestMapping(value = "/admin/download")
@Slf4j
@RequiredArgsConstructor
public class DownloadController {

    private final Y9logUserLoginInfoService userLoginInfoService;
    private final PersonApiClient personManager;

    @ResponseBody
    @RequestMapping(value = "/exportNotLoginXLS", method = RequestMethod.GET)
    public void exportNotLoginXLS(String tenantID, HttpServletResponse response) {
        if (StringUtils.isNotBlank(tenantID)) {
            Y9LoginUserHolder.setTenantId(tenantID);
        }

        try (OutputStream outStream = response.getOutputStream();
            InputStream in = new ClassPathResource("/template/exportSimpleTemplate.xlsx").getInputStream()) {

            Map<String, Object> map = xlsLoginData2(tenantID);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(
                "有生云未登录信息-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx"));

            JxlsUtil jxlsUtil = new JxlsUtil();
            jxlsUtil.exportExcel(in, outStream, map);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/exportLoginXLS", method = RequestMethod.GET)
    public void exportPersonXLS(String tenantID, HttpServletResponse response) {
        if (StringUtils.isNotBlank(tenantID)) {
            Y9LoginUserHolder.setTenantId(tenantID);
        }
        try (OutputStream outStream = response.getOutputStream();
            InputStream in = new ClassPathResource("/template/exportSimpleTemplate.xlsx").getInputStream()) {
            Map<String, Object> map = xlsLoginData(tenantID);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(
                "有生云登录信息-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx"));

            JxlsUtil jxlsUtil = new JxlsUtil();
            jxlsUtil.exportExcel(in, outStream, map);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public String reverseSplit(String path) {
        if (!path.contains(",")) {
            return path;
        }
        String[] oldString = path.split(",");
        StringBuffer strBuffer = new StringBuffer();
        for (int lenth = oldString.length; lenth > 0; lenth--) {
            strBuffer.append(oldString[lenth - 1]);
            strBuffer.append(",");
        }
        String newString = strBuffer.toString();
        return newString.substring(0, newString.lastIndexOf(","));
    }

    public Map<String, Object> xlsLoginData(String tenantID) {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterable<Y9logUserLoginInfo> userLoginInfoList = userLoginInfoService.listAll();
        Iterator<Y9logUserLoginInfo> iterator = userLoginInfoList.iterator();
        List<String> userIDList = new ArrayList<String>();
        while (iterator.hasNext()) {
            Y9logUserLoginInfo info = iterator.next();
            String userId = info.getUserId();
            if (!userIDList.contains(userId)) {
                userIDList.add(userId);
            }
        }

        List<Person> persons = personManager.listAllPersons(tenantID).getData();
        List<PersonInformation> personInformationList = new ArrayList<PersonInformation>();
        for (Person person : persons) {
            if (person.getDisabled()) {
                continue;
            }
            String personId = person.getId();
            if (userIDList.contains(personId)) {
                PersonInformation pf = new PersonInformation();
                String fullPath = person.getDn().replaceAll("cn=", "").replaceAll(",ou=", ",").replaceAll(",o=", ",");
                String path = fullPath.substring(0, fullPath.lastIndexOf(","));
                if (path.contains(",") && fullPath.lastIndexOf(",") == fullPath.lastIndexOf(",")) {
                    path = path.substring(fullPath.indexOf(",") + 1);
                    pf.setFullPath(reverseSplit(path));
                } else if (!path.contains(",")) {
                    pf.setFullPath(null);
                } else {
                    path = path.substring(fullPath.indexOf(",") + 1, fullPath.lastIndexOf(","));
                    pf.setFullPath(reverseSplit(path));
                }
                pf.setName(fullPath.substring(0, fullPath.indexOf(",")));
                pf.setEmail(person.getEmail());
                pf.setLoginName(person.getLoginName());
                pf.setMobile(person.getMobile());
                pf.setSex(person.getSex().getDescription());
                personInformationList.add(pf);
            }

        }
        map.put("personList", personInformationList);
        return map;
    }

    public Map<String, Object> xlsLoginData2(String tenantID) {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterable<Y9logUserLoginInfo> userLoginInfoList = userLoginInfoService.listAll();
        Iterator<Y9logUserLoginInfo> iterator = userLoginInfoList.iterator();
        List<String> userIDList = new ArrayList<String>();
        while (iterator.hasNext()) {
            Y9logUserLoginInfo info = iterator.next();
            String userId = info.getUserId();
            if (!userIDList.contains(userId)) {
                userIDList.add(userId);
            }
        }

        List<Person> persons = personManager.listAllPersons(tenantID).getData();
        List<PersonInformation> personInformationList = new ArrayList<PersonInformation>();
        for (Person person : persons) {
            if (person.getDisabled()) {
                continue;
            }
            String personId = person.getId();
            if (userIDList.contains(personId)) {
                continue;
            }
            PersonInformation pf = new PersonInformation();
            String fullPath = person.getDn().replaceAll("cn=", "").replaceAll(",ou=", ",").replaceAll(",o=", ",");
            String path = fullPath.substring(0, fullPath.lastIndexOf(","));
            if (path.contains(",") && fullPath.lastIndexOf(",") == fullPath.lastIndexOf(",")) {
                path = path.substring(fullPath.indexOf(",") + 1);
                pf.setFullPath(reverseSplit(path));
            } else if (!path.contains(",")) {
                pf.setFullPath(null);
            } else {
                path = path.substring(fullPath.indexOf(",") + 1, fullPath.lastIndexOf(","));
                pf.setFullPath(reverseSplit(path));
            }
            pf.setName(fullPath.substring(0, fullPath.indexOf(",")));
            pf.setEmail(person.getEmail());
            pf.setLoginName(person.getLoginName());
            pf.setMobile(person.getMobile());
            pf.setSex(person.getSex().getDescription());
            personInformationList.add(pf);
        }
        map.put("personList", personInformationList);
        return map;
    }

    public Map<String, Object> xlsLoginDataLogin() {
        Map<String, Object> map = new HashMap<>();
        Iterable<Y9logUserLoginInfo> userLoginInfoList = userLoginInfoService.listAll();
        Iterator<Y9logUserLoginInfo> iterator = userLoginInfoList.iterator();
        List<String> userIDList = new ArrayList<>();
        List<LoginInformation> personInformationList = new ArrayList<>();
        while (iterator.hasNext()) {
            Y9logUserLoginInfo info = iterator.next();
            String userId = info.getUserId();
            LoginInformation login = new LoginInformation();
            login.setTenantId(info.getTenantId());
            login.setUserId(userId);
            login.setUserName(info.getUserName());
            if (!userIDList.contains(userId)) {
                userIDList.add(userId);
                personInformationList.add(login);
            }
        }

        map.put("personList", personInformationList);
        return map;
    }
}
