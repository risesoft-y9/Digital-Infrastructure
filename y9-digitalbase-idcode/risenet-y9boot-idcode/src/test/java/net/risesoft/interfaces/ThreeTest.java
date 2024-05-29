package net.risesoft.interfaces;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.model.OrganUnitResult;
import net.risesoft.model.OrganUnitStatusInfo;
import net.risesoft.model.RegistResult;
import net.risesoft.model.Result;
import net.risesoft.model.SmsVerifyCode;
import net.risesoft.model.UnitRegistResult;
import net.risesoft.model.UpdateGotoUrlResult;
import net.risesoft.util.ConfigReader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@DisplayName("单位用户注册相关接口")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ThreeTest {

    @Test
    @Order(1)
    @DisplayName("【301】单位注册信息提交接口1")
    @Disabled
    void testCompanyinfoReg() {
        RegistResult result = Three.companyinfoReg("loginName", "loginPassword", "email", "orgUnitName", "orgUnitEnName", 1, 1, 1, 1, "linkman", "linkmanEn", "linkPhone", "smsVerifyCode");
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("单位主码:{}", result.getIdCode());
        }
    }

    @Test
    @Order(2)
    @DisplayName("【302】获取激活验证码接口(SP平台发短信）")
    @Disabled
    void testVerifyCode() {
        SmsVerifyCode result = Three.verifyCode("13049881814");
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("验证码:{}", result.getVerifyCode());
        }
    }

    @Test
    @Order(3)
    @DisplayName("【303】发送激活验证码接口(IDcode平台发短信）")
    @Disabled
    void testVerifyCodeSend() {
        Result result = Three.verifyCodeSend("13049881814");
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("结果信息:{}", result.getResultMsg());
        }
    }

    @Test
    @Order(4)
    @DisplayName("【304】单位认证接口")
    @Disabled
    void testCompanyinfoVerify() {
        Result result = Three.companyinfoVerify(ConfigReader.MAIN_CODE, 2, "51110000500317549R",new File("D:/1111.jpg"));
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("结果信息:{}", result.getResultMsg());
        }
    }

    @Test
    @Order(5)
    @DisplayName("【305】单位资料完善相关接口")
    @Disabled
    void testCompanyinfoModify() {
        Result result = Three.companyinfoModify(ConfigReader.MAIN_CODE, 1, "address", "name", "nameEn", "addressEn", "email", "linkman", "linkmanEn", "fax", "workAddress", "workAddressEn", 1, 1, 1, "gotoUrl", "linkPhone", new File("D:/1111.jpg"));
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("结果信息:{}", result.getResultMsg());
        }
    }

    @Test
    @Order(6)
    @DisplayName("【306】获取单位基本信息接口")
    void testCompanyinfoBase() {
        Result result = Three.companyinfoBase(ConfigReader.MAIN_CODE);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("结果信息:{}", result.getResultMsg());
        }
    }

    @Test
    @Order(7)
    @DisplayName("【307】根据单位名称获取单位基本信息接口")
    void testCompanyinfoSearch() {
        OrganUnitResult result = Three.companyinfoSearch("中关村软件", "0");
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(unit -> LOGGER.debug("单位名称:{}", unit.getName()));
        }
    }

    @Test
    @Order(8)
    @DisplayName("【308】获取单位状态接口")
    void testOrganUnitStatus() {
        OrganUnitStatusInfo result = Three.organUnitStatus(ConfigReader.MAIN_CODE);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("审核状态:{} 认证状态:{} 剩余时间:{}", result.getAudit(), result.getAuthen(), result.getSurplusTime());
        }
    }

    @Test
    @Order(9)
    @DisplayName("【309】单位注册信息提交接口2")
    @Disabled
    void testUnitRegist() {
        UnitRegistResult result = Three.unitRegist("loginName", "orgUnitName", "email", "code", "provinceId", "cityId", "areaId", "linkman", "linkPhone", new File("D:/1111.jpg"), "qrCodeColor", new File("D:/1111.jpg"), "gotoUrl", "qrCodeSize");
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("单位主码:{} 二维码:{} 登录名:{}", result.getIdCode(), result.getQrCode(), result.getLoginName());
        }
    }

    @Test
    @Order(9)
    @DisplayName("【310】修改单位logo或企业码解析地址")
    @Disabled
    void testCompanyIdCodeGotoUrl() {
        UpdateGotoUrlResult result = Three.companyIdCodeGotoUrl("loginName", "pwd", "1111", "gotoUrl", 1, 1, 1);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("结果信息:{}", result.getQrCode());
        }
    }
}
