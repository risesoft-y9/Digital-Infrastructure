package net.risesoft.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.permission.annotation.IsManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.json.Y9JsonUtil;

@RestController
@RequestMapping(value = "/api/rest/gitee", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@IsManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER, ManagerLevelEnum.AUDIT_MANAGER})
@Slf4j
public class GiteeController {

    @GetMapping("/isStarred")
    public Y9Result<Boolean> isStarred(String code) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost accessTokenRequest = new HttpPost("https://gitee.com/oauth/token");
        String accessToken = "";

        List<NameValuePair> paramList = new ArrayList<>();
        paramList.add(new BasicNameValuePair("grant_type", "authorization_code"));
        paramList.add(new BasicNameValuePair("code", code));
        paramList.add(
            new BasicNameValuePair("client_id", "e05fe4423a35a22d85978c0c28c6acceda1c7636201f83a5e86545503041ae5a"));
        paramList.add(new BasicNameValuePair("client_secret",
            "b3132c51b80af4c8e8c6101ce38681b37acc5b1ca6b075f2051ecab5e1cd4914"));
        paramList.add(
            new BasicNameValuePair("redirect_uri", "https://test.youshengyun.com/kernel-standard/?platform=gitee"));
        accessTokenRequest.setEntity(new UrlEncodedFormEntity(paramList));
        try (CloseableHttpResponse response = httpClient.execute(accessTokenRequest)) {
            String responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");
            LOGGER.debug("gitee oauth token response: {}", responseContent);

            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 200:
                    AccessTokenResponse accessTokenResponse =
                        Y9JsonUtil.readValue(responseContent, AccessTokenResponse.class);
                    accessToken = accessTokenResponse.getAccessToken();
                    break;
                case 401:
                    return Y9Result.success(false);
            }
        }

        HttpGet checkStarredRequest =
            new HttpGet("https://gitee.com/api/v5/user/starred/risesoft-y9/y9-core?access_token=" + accessToken);
        try (CloseableHttpResponse response = httpClient.execute(checkStarredRequest)) {
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 204:
                    return Y9Result.success(true);
                case 404:
                    return Y9Result.success(false);
            }
        }
        return Y9Result.success(false);
    }

    @Data
    static class AccessTokenResponse implements Serializable {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private Integer expiresIn;

        @JsonProperty("refresh_token")
        private String refreshToken;

        private String scope;
    }
}
