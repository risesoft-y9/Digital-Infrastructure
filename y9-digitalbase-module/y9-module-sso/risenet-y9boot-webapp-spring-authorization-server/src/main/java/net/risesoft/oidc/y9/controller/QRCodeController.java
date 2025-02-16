package net.risesoft.oidc.y9.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import net.risesoft.oidc.y9.service.Y9KeyValueService;
import net.risesoft.oidc.util.Y9Base64;
import net.risesoft.oidc.util.Y9QRCode;
import net.risesoft.oidc.util.Y9Result;
import net.risesoft.oidc.util.common.Y9Util;
import net.risesoft.oidc.util.json.Y9JacksonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Lazy(false)
@RestController
@RequestMapping(value = "/api")
@Slf4j
@RequiredArgsConstructor
public class QRCodeController {
    private final Y9KeyValueService y9KeyValueService;

    @PostMapping(value = "/getQRCode")
    public Map<String, Object> getQRCode(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("img", "");
        map.put("uuid", "");
        map.put("success", false);
        try {
            String baseUrl = Y9Util.requestBaseUrl(request);
            String uuid = UUID.randomUUID().toString();
            String url = baseUrl + "login?url=" + baseUrl + "api/saveScanResult&uuid=" + uuid;
            InputStream imgis = this.getClass().getClassLoader().getResourceAsStream("static/y9static/y9new/img/qrCodeLogo.png");
            String img = Y9QRCode.encode(url, 512, 512, imgis);
            y9KeyValueService.put("QRCode:" + uuid, "2", 2);
            map.put("img", img);
            map.put("uuid", uuid);
            map.put("success", true);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    @PostMapping(value = "/getScanResult")
    public Map<String, Object> getScanResult(String uuid) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("userId", "");
        map.put("msg", "获取扫描结果失败");
        try {
            String value = y9KeyValueService.get("QRCode:" + uuid);
            if (null != value) {
                if (value.contains("$")) {
                    String[] valArr = value.split("\\$");
                    map.put("scanResult", valArr[0]);
                    map.put("userId", valArr[1]);
                    map.put("success", true);
                } else {
                    map.put("success", true);
                    map.put("scanResult", value);
                }
                map.put("msg", "获取扫描结果成功");
            } else {
                map.put("scanResult", 0);
                map.put("msg", "uuid对应的二维码已过期");
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return map;
    }

    @PostMapping(value = "/saveScanResult")
    public void saveScanResult(String uuid, String userId, HttpServletResponse response) {
        try {
            if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(userId)) {
                Y9Util.renderJson(response,
                    Y9JacksonUtil.writeValueAsString(Y9Result.failure(400, "请求参数有问题：uuid或userId不能为空")));
                return;
            }
            userId = Y9Base64.decode(userId);
            String obj = y9KeyValueService.get("QRCode:" + uuid);
            if (null != obj) {
                if (obj.contains("1$")) {
                    Y9Util.renderJson(response,
                        Y9JacksonUtil.writeValueAsString(Y9Result.failure(419, "二维码已过期：已被扫描。")));
                } else {
                    y9KeyValueService.put("QRCode:" + uuid, 1 + "$" + userId, 5);
                    Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(Y9Result.successMsg("扫码成功")));
                }
            } else {
                Y9Util.renderJson(response,
                    Y9JacksonUtil.writeValueAsString(Y9Result.failure(419, "二维码已过期：uuid已过期自动清除。")));
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

    }
}