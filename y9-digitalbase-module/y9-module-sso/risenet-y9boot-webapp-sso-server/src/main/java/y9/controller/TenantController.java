package y9.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import y9.controller.dto.Tenant;
import y9.entity.Y9User;
import y9.service.Y9TenantService;
import y9.service.Y9UserService;
import y9.util.MobileUtil;
import y9.util.common.XSSCheckUtil;
import y9.util.json.Y9JacksonUtil;

@Lazy(false)
@Controller
@RequestMapping(value = "/api")
@Slf4j
@RequiredArgsConstructor
public class TenantController {

    private final Y9UserService y9UserService;

    private final Y9TenantService y9TenantService;

    @GetMapping(value = "/allTenants")
    public ResponseEntity<String> allTenants() {
        try {
            List<Map<String, String>> tenants = y9UserService.listAllTenants();
            List<Tenant> tenants1 = convertAndSort(tenants);

            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(tenants1);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(jsonStr, headers, HttpStatus.OK);
        } catch (final Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 根据登录名，获取租户信息列表（目前手机端在用）
     *
     * @param loginName
     * @return
     */
    @RequestMapping(value = "/tenants")
    public final ResponseEntity<String> getTenants(@RequestParam String loginName) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            loginName = XSSCheckUtil.filter(loginName);
            if (MobileUtil.isMobile(loginName)) {
                List<Y9User> users = y9UserService.findByMobileAndOriginal(loginName, Boolean.TRUE);
                if (users.size() > 0) {
                    for (Y9User user : users) {
                        Map<String, Object> mapTemp = new HashMap<>();
                        mapTemp.put("tenantShortName", user.getTenantShortName());
                        mapTemp.put("tenantName", user.getTenantName());
                        list.add(mapTemp);
                    }
                }
            } else {
                List<Y9User> users = y9UserService.findByLoginNameAndOriginal(loginName, Boolean.TRUE);
                if (!users.isEmpty()) {
                    for (Y9User user : users) {
                        Map<String, Object> mapTemp = new HashMap<>();
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
            return new ResponseEntity<>(jsonStr, headers, HttpStatus.OK);
        } catch (final Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/singleTenant")
    public ResponseEntity<String> singleTenant() {
        try {
            List<Map<String, String>> tenants = y9UserService.listAllTenants();
            List<Tenant> tenants1 = convertAndSort(tenants);

            Optional<Tenant> tenantOptional = tenants1.stream().findFirst();

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(Y9JacksonUtil.writeValueAsString(tenantOptional.get()), headers, HttpStatus.OK);
        } catch (final Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private List<Tenant> convertAndSort(List<Map<String, String>> tenantMapList) {
        List<Tenant> originalTenantList = tenantMapList.stream()
            .map(map -> new Tenant(map.get("tenantName"), map.get("tenantShortName"))).collect(Collectors.toList());

        boolean isOperationTenantExist = false;
        List<Tenant> newTenantList = new ArrayList<>();

        for (Tenant tenant : originalTenantList) {
            if (Tenant.OPERATION_TENANT.equals(tenant)) {
                isOperationTenantExist = true;
            } else {
                newTenantList.add(tenant);
            }
        }

        // 运维租户总是放在最后
        if (isOperationTenantExist) {
            newTenantList.add(Tenant.OPERATION_TENANT);
        }

        return newTenantList;
    }
}
