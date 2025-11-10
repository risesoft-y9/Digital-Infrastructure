package net.risesoft.interfaces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.IdCodeConfig;
import net.risesoft.model.OrganUnitResult;
import net.risesoft.model.OrganUnitStatusInfo;
import net.risesoft.model.RegistResult;
import net.risesoft.model.Result;
import net.risesoft.model.SmsVerifyCode;
import net.risesoft.model.UnitRegistResult;
import net.risesoft.model.UpdateGotoUrlResult;
import net.risesoft.util.Config;

@SpringBootTest
@Slf4j
@DisplayName("单位用户注册相关接口")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ThreeTest {

    @Autowired
    private Environment environment;

    @BeforeEach
    public void setUp() {
        IdCodeConfig.init(environment.getProperty("idCode.api_code"), environment.getProperty("idCode.api_key"),
            environment.getProperty("idCode.idCode_url"), environment.getProperty("idCode.main_code"),
            environment.getProperty("idCode.analyze_url"), environment.getProperty("idCode.goto_url"),
            environment.getProperty("idCode.sample_url"));
    }

    @Test
    @Order(1)
    @DisplayName("【301】单位注册信息提交1")
    @Disabled
    void testM301() {
        RegistResult result = Three.m301("loginName", "loginPassword", "email", "orgUnitName", "orgUnitEnName", 1, 1, 1,
            1, "linkman", "linkmanEn", "linkPhone", "smsVerifyCode");
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("单位主码:{}", result.getIdCode());
        }
    }

    @Test
    @Order(2)
    @DisplayName("【302】获取激活验证码(SP平台发短信）")
    @Disabled
    void testM302() {
        SmsVerifyCode result = Three.m302("13049881814");
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("验证码:{}", result.getVerifyCode());
        }
    }

    @Test
    @Order(3)
    @DisplayName("【303】发送激活验证码(IDcode平台发短信）")
    @Disabled
    void testM303() {
        Result result = Three.m303("13049881814");
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("结果信息:{}", result.getResultMsg());
        }
    }

    @Test
    @Order(4)
    @DisplayName("【304】单位认证")
    @Disabled
    void testM304() {
        Result result = Three.m304(Config.MAIN_CODE, 2, "51110000500317549R", new File("D:/1111.jpg"));
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("结果信息:{}", result.getResultMsg());
        }
    }

    @Test
    @Order(5)
    @DisplayName("【305】单位资料完善")
    @Disabled
    void testM305() {
        Result result =
            Three.m305(Config.MAIN_CODE, 1, "address", "name", "nameEn", "addressEn", "email", "linkman", "linkmanEn",
                "fax", "workAddress", "workAddressEn", 1, 1, 1, Config.GOTO_URL, "linkPhone", new File("D:/1111.jpg"));
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("结果信息:{}", result.getResultMsg());
        }
    }

    @Test
    @Order(6)
    @DisplayName("【306】获取单位基本信息")
    void testM306() {
        Result result = Three.m306(Config.MAIN_CODE);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("结果信息:{}", result.getResultMsg());
        }
    }

    @Test
    @Order(7)
    @DisplayName("【307】根据单位名称获取单位基本信息")
    void testM307() {
        OrganUnitResult result = Three.m307("中关村软件", "0");
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(unit -> LOGGER.debug("单位名称:{}", unit.getName()));
        }
    }

    @Test
    @Order(8)
    @DisplayName("【308】获取单位状态")
    void testM308() {
        OrganUnitStatusInfo result = Three.m308(Config.MAIN_CODE);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("审核状态:{} 认证状态:{} 剩余时间:{}", result.getAudit(), result.getAuthen(), result.getSurplusTime());
        }
    }

    @Test
    @Order(9)
    @DisplayName("【309】单位注册信息提交2")
    @Disabled
    void testM309() {
        UnitRegistResult result = Three.m309("loginName", "orgUnitName", "email", "code", "provinceId", "cityId",
            "areaId", "linkman", "linkPhone", new File("D:/1111.jpg"), "qrCodeColor", new File("D:/1111.jpg"),
            Config.GOTO_URL, "qrCodeSize");
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("单位主码:{} 二维码:{} 登录名:{}", result.getIdCode(), result.getQrCode(), result.getLoginName());
        }
    }

    @Test
    @Order(10)
    @DisplayName("【310】修改单位logo或企业码解析地址")
    @Disabled
    void testM310() {
        UpdateGotoUrlResult result = Three.m310("loginName", "pwd", "1111", Config.GOTO_URL, 1, 1, 1);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("结果信息:{}", result.getQrCode());
        }
    }
}
