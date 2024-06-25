package net.risesoft.interfaces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.AuthenPicResult;
import net.risesoft.model.ExamineResult;
import net.risesoft.model.OrganUnit;
import net.risesoft.model.Result;
import net.risesoft.util.ConfigReader;

@SpringBootTest
@Slf4j
@DisplayName("获取认证图片接口")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SevenTest {

    @Test
    @Order(1)
    @Disabled
    @DisplayName("【601】单位登录验证")
    public void testM601() {
        String loginName = "zgcsa";
        String loginPswd = "zgcsa";
        OrganUnit result = Seven.m601(loginName, loginPswd);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("单位登录验证结果:{}", result);
        }
    }

    @Test
    @Order(2)
    @Disabled
    @DisplayName("【602】修改解析地址")
    public void testM602() {
        String idCode = ConfigReader.MAIN_CODE;
        String gotoUrl = "";
        String sampleUrl = "";
        String regId = "";
        Result result = Seven.m602(idCode, gotoUrl, sampleUrl, regId);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("码图地址:{}", result);
        }
    }

    @Test
    @Order(3)
    @Disabled
    @DisplayName("【606】申请IDcode解析地址白名单")
    public void testM606() {
        String gotoUrl = "";
        String sampleUrl = "";
        Result result = Seven.m606(gotoUrl, sampleUrl);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("码图地址:{}", result);
        }
    }

    /*@Test
    @Order(4)
    @Disabled
    @DisplayName("【607】单位登录页面接口(页面接口)")
    public void testM607() {
        String callbackUrl = "www.baidu.com";
        Result result = Seven.m607(callbackUrl);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("码图地址:{}", result);
        }
    }*/

    @Test
    @Order(5)
    @Disabled
    @DisplayName("【608】查询审核状态")
    public void testM608() {
        String idCode = ConfigReader.MAIN_CODE;
        String categoryRegId = "";
        Integer type = 1;
        ExamineResult result = Seven.m608(idCode, categoryRegId, type);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("审核状态:{}", result);
        }
    }

    @Test
    @Order(1)
    @DisplayName("【701】获取认证图片")
    void testM701() {
        Integer bgImage = 0;
        Integer isMarkName = 1;
        AuthenPicResult result = Seven.m701(bgImage, isMarkName);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("认证图片地址URL:{}", result.getUrl());
        }
    }
}
