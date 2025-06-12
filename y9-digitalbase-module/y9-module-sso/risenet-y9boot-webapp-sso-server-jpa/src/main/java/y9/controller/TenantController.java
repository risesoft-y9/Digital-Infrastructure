package y9.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import y9.controller.dto.Tenant;
import y9.service.Y9UserService;

@Lazy(false)
@Controller
@RequestMapping(value = "/api")
@Slf4j
@RequiredArgsConstructor
public class TenantController {

    private final Y9UserService y9UserService;

    @GetMapping(value = "/allTenants")
    public final ResponseEntity<String> allTenants() {
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
