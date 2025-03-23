package y9.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import y9.entity.Y9User;
import y9.service.Y9UserService;
import y9.util.MobileUtil;
import y9.util.common.XSSCheckUtil;

@Lazy(false)
@Controller
@RequestMapping(value = "/api")
@Slf4j
@RequiredArgsConstructor
public class TenantController {

    private final Y9UserService y9UserService;

    /**
     * 根据登录名，获取登录信息，返回信息如（XXX@XXX）
     * 
     * @param loginName
     * @return
     */
    @RequestMapping(value = "/loginNameAndTenants")
    public final ResponseEntity<String> getLoginNameAndTenants(@RequestParam String loginName) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            loginName = XSSCheckUtil.filter(loginName);
            List<Y9User> y9users = y9UserService.findByOriginalAndLoginNameStartingWith(Boolean.TRUE, loginName);
            y9users.sort(Comparator.comparing(Y9User::getOrderedPath, Comparator.nullsFirst(Comparator.naturalOrder())));
            if (!y9users.isEmpty()) {
                for (Y9User user : y9users) {
                    Map<String, Object> mapTemp = new HashMap<String, Object>();
                    mapTemp.put("tenantShortName", user.getTenantShortName());
                    mapTemp.put("tenantName", user.getTenantName());
                    mapTemp.put("loginName", user.getLoginName() + "@" + user.getTenantName());
                    list.add(mapTemp);
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(list);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(jsonStr, headers, HttpStatus.OK);
        } catch (final Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 根据登录名，获取租户信息列表
     * 
     * @param loginName
     * @return
     */
    @RequestMapping(value = "/tenants")
    public final ResponseEntity<String> getTenants(@RequestParam String loginName) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            loginName = XSSCheckUtil.filter(loginName);
            if (MobileUtil.isMobile(loginName)) {
                List<Y9User> users = y9UserService.findByMobileAndOriginal(loginName, Boolean.TRUE);
                if (users.size() > 0) {
                    for (Y9User user : users) {
                        Map<String, Object> mapTemp = new HashMap<String, Object>();
                        mapTemp.put("tenantShortName", user.getTenantShortName());
                        mapTemp.put("tenantName", user.getTenantName());
                        list.add(mapTemp);
                    }
                }
            } else {
                List<Y9User> users = y9UserService.findByLoginNameAndOriginal(loginName, Boolean.TRUE);
                if (!users.isEmpty()) {
                    for (Y9User user : users) {
                        Map<String, Object> mapTemp = new HashMap<String, Object>();
                        mapTemp.put("tenantShortName", user.getTenantShortName());
                        mapTemp.put("tenantName", user.getTenantName());
                        list.add(mapTemp);
                    }
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(list);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(jsonStr, headers, HttpStatus.OK);
        } catch (final Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 根据登录名和租户名称，获取租户信息列表
     * 
     * @param loginName
     * @param tenantName
     * @return
     */
    @RequestMapping(value = "/getTenants")
    public final ResponseEntity<String> getTenants(@RequestParam String loginName, @RequestParam String tenantName) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            loginName = XSSCheckUtil.filter(loginName);
            tenantName = XSSCheckUtil.filter(tenantName);
            List<Y9User> users = y9UserService.findByTenantNameAndLoginNameAndOriginal(tenantName, loginName, Boolean.TRUE);
            if (users.size() > 0) {
                for (Y9User user : users) {
                    Map<String, Object> mapTemp = new HashMap<String, Object>();
                    mapTemp.put("tenantShortName", user.getTenantShortName());
                    mapTemp.put("tenantName", user.getTenantName());
                    list.add(mapTemp);
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(list);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(jsonStr, headers, HttpStatus.OK);
        } catch (final Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 根据登录名，获取登录信息，返回信息如（XXX@XXX）
     * 
     * @param loginName
     * @return
     */
    @RequestMapping(value = "/getTenantUsers")
    public final ResponseEntity<String> getTenantUsers(@RequestParam String loginName) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            loginName = XSSCheckUtil.filter(loginName);
            List<String> tenentlist = new ArrayList<String>();
            tenentlist.add("isv");
            tenentlist.add("operation");

            List<Y9User> users = y9UserService.findByTenantShortNameNotInAndLoginNameAndOriginal(tenentlist, loginName, Boolean.TRUE);
            if (!users.isEmpty()) {
                for (Y9User user : users) {
                    Map<String, Object> mapTemp = new HashMap<String, Object>();
                    mapTemp.put("tenantShortName", user.getTenantShortName());
                    mapTemp.put("tenantName", user.getTenantName());
                    mapTemp.put("loginName", user.getLoginName() + "@" + user.getTenantName());
                    list.add(mapTemp);
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(list);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(jsonStr, headers, HttpStatus.OK);
        } catch (final Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 根据登录名，获取登录信息，返回信息如（XXX@XXX）
     * 
     * @param loginName
     * @return
     */
    @RequestMapping(value = "/getByLoginNameAndTenantShortName")
    public final ResponseEntity<String> getUsersByLoginNameAndTenantShortName(@RequestParam String loginName, @RequestParam String tenantShortName) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            loginName = XSSCheckUtil.filter(loginName);
            tenantShortName = XSSCheckUtil.filter(tenantShortName);

            List<Y9User> users = y9UserService.findByTenantShortNameAndLoginNameAndOriginal(tenantShortName, loginName, Boolean.TRUE);
            if (!users.isEmpty()) {
                for (Y9User user : users) {
                    Map<String, Object> mapTemp = new HashMap<String, Object>();
                    mapTemp.put("tenantShortName", user.getTenantShortName());
                    mapTemp.put("tenantName", user.getTenantName());
                    mapTemp.put("loginName", user.getLoginName() + "@" + user.getTenantName());
                    list.add(mapTemp);
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(list);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(jsonStr, headers, HttpStatus.OK);
        } catch (final Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 根据登录名，获取租户信息，（用户密码修改）
     * 
     * @param loginName
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/tenants4ModifyPassword")
    public List<Map<String, Object>> tenants4ModifyPassword(@RequestParam String loginName) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            loginName = XSSCheckUtil.filter(loginName);

            List<Y9User> users = y9UserService.findByLoginNameContainingAndOriginalOrderByTenantShortName(loginName, Boolean.TRUE);
            if (!users.isEmpty()) {
                for (Y9User user : users) {
                    Map<String, Object> mapTemp = new HashMap<String, Object>();
                    mapTemp.put("tenantID", user.getTenantId());
                    mapTemp.put("tenantName", user.getTenantName());
                    list.add(mapTemp);
                }
            }

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return list;
    }

}
