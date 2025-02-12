package net.risesoft.controller;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.platform.Person;
import net.risesoft.pojo.PersonInformation;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.mime.ContentDispositionUtil;
import net.risesoft.y9public.entity.Y9logUserLoginInfo;
import net.risesoft.y9public.service.Y9logUserLoginInfoService;

import y9.client.rest.platform.org.PersonApiClient;

import cn.idev.excel.FastExcel;

/**
 * 下载管理
 */
@Controller
@RequestMapping(value = "/admin/download")
@Slf4j
@RequiredArgsConstructor
public class DownloadController {

    private final Y9logUserLoginInfoService userLoginInfoService;
    private final PersonApiClient personManager;

    /**
     * 下载未登录信息
     * 
     * @param tenantId 租户id
     * @param response 响应信息
     */
    @ResponseBody
    @RequestMapping(value = "/exportNotLoginXLS", method = RequestMethod.GET)
    public void exportNotLoginXLS(String tenantId, HttpServletResponse response) {
        if (StringUtils.isNotBlank(tenantId)) {
            Y9LoginUserHolder.setTenantId(tenantId);
        }

        try (OutputStream outStream = response.getOutputStream()) {

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(
                "有生云未登录信息-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx"));

            FastExcel.write(outStream, PersonInformation.class).sheet().doWrite(notLoginData(tenantId));

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 下载已登录信息
     * 
     * @param tenantId 租户id
     * @param response 响应信息
     */
    @ResponseBody
    @RequestMapping(value = "/exportLoginXLS", method = RequestMethod.GET)
    public void exportPersonXLS(String tenantId, HttpServletResponse response) {
        if (StringUtils.isNotBlank(tenantId)) {
            Y9LoginUserHolder.setTenantId(tenantId);
        }
        try (OutputStream outStream = response.getOutputStream()) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(
                "有生云登录信息-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx"));

            FastExcel.write(outStream, PersonInformation.class).sheet().doWrite(loginData(tenantId));

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

    public List<PersonInformation> loginData(String tenantID) {
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

        List<Person> persons = personManager.list(tenantID).getData();
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
        return personInformationList;
    }

    public List<PersonInformation> notLoginData(String tenantID) {
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

        List<Person> persons = personManager.list(tenantID).getData();
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
        return personInformationList;
    }
}
