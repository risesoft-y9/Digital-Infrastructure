package net.risesoft.interfaces;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.model.BaseIdCodeInfo;
import net.risesoft.model.BatchRegistResult;
import net.risesoft.model.IdcodeRegResult;
import net.risesoft.model.Result;
import net.risesoft.util.ConfigReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@DisplayName("品类注册/备案相关接口")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FourTest {

    @BeforeEach
    public void setUp() {

    }

    @Test
    @Order(1)
    @Disabled
    @DisplayName("【401】注册品类接口(页面接口)")
    public void testM401() {
        String gotoUrl = "http://example.com/goto";
        String sampleUrl = "http://example.com/goto";
        String callbackUrl = "http://example.com/callback";
        Result result = Four.m401(ConfigReader.MAIN_CODE, gotoUrl, sampleUrl, callbackUrl);
        assertEquals(result.getResultCode(), 1);
        // 可以在这里添加更多的断言来验证结果的正确性
    }

    @Test
    @Order(2)
    @Disabled
    @DisplayName("【402】批量注册/备案品类接口")
    public void testM402() {
        String jsonStr = "{\"key\": \"value\"}";
        BatchRegistResult result = Four.m402(jsonStr);
        assertEquals(result.getResultCode(), 1);
        // 可以在这里添加更多的断言来验证结果的正确性
    }

    @Test
    @Order(3)
    @DisplayName("【403】查询注册品类")
    public void testM403() {
        String idCodeOfCategory = "";
        String modelNumberCode = "";
        String categoryRegId = "";
        BaseIdCodeInfo result = Four.m403(ConfigReader.MAIN_CODE, idCodeOfCategory, modelNumberCode, categoryRegId);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("主键ID:{} 品类用途编码:{} 品类码号:{} 品类描述:{} 完整码:{}", item.getId(), item.getCodeUseId(), item.getCategoryCode(), item.getCategoryMemo(),item.getCompleteCode()));
        }
    }

    @Test
    @Order(4)
    @DisplayName("【404】查询所有注册品类")
    public void testM404() {
        Integer searchType = 1;
        BaseIdCodeInfo result = Four.m404(ConfigReader.MAIN_CODE, searchType);
        assertEquals(result.getResultCode(), 1);
        // 可以在这里添加更多的断言来验证结果的正确性
    }

    @Test
    @Order(5)
    @Disabled
    @DisplayName("【405】注册/备案产品品类IDcode码接口(页面接口)")
    public void testM405() {
        String industryCategoryId = "789012";
        String codePayType = "345678";
        String gotoUrl = "http://example.com/goto";
        String sampleUrl = "http://example.com/sample";
        String callbackUrl = "http://example.com/callback";
        Result result = Four.m405(ConfigReader.MAIN_CODE, industryCategoryId, codePayType, gotoUrl, sampleUrl, callbackUrl);
        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(6)
    @Disabled
    @DisplayName("【406】注册/备案产品品类IDcode码接口")
    public void testM406() {
        // 准备参数
        String codeUseId = "789012";
        Integer industryCategoryId = 3;
        String categoryCode = "456789";
        String modelNumber = "789012";
        String modelNumberCode = "345678";
        Integer codePayType = 1;
        String gotoUrl = "http://example.com/goto";
        String sampleUrl = "http://example.com/sample";

        IdcodeRegResult result = Four.m406(ConfigReader.MAIN_CODE, codeUseId, industryCategoryId, categoryCode, modelNumber, modelNumberCode, codePayType, gotoUrl, sampleUrl);

        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(7)
    @Disabled
    @DisplayName("【407】注册/备案非产品品类IDcode码接口")
    public void testM407() {
        // 准备参数
        String codeUseId = "789012";
        Integer industryCategoryId = 3;
        String categoryCode = "456789";
        String modelNumber = "789012";
        String modelNumberEn = "English Model Number";
        String introduction = "This is an introduction.";
        Integer codePayType = 1;
        String gotoUrl = "http://example.com/goto";
        String sampleUrl = "http://example.com/sample";

        // 调用方法
        IdcodeRegResult result = Four.m407(ConfigReader.MAIN_CODE, codeUseId, industryCategoryId, categoryCode, modelNumber, modelNumberEn, introduction, codePayType, gotoUrl, sampleUrl);

        // 断言结果
        assertEquals(result.getResultCode(), 1);
        // 可以在这里添加更多的断言来验证结果的正确性
    }

    @Test
    @Order(8)
    @Disabled
    @DisplayName("【408】备案品类接口(页面接口) ")
    public void testM408() {
        String gotoUrl = "http://example.com/goto";
        String sampleUrl = "http://example.com/sample";
        String callbackUrl = "http://example.com/callback";
        Result result = Four.m408(ConfigReader.MAIN_CODE, gotoUrl, sampleUrl, callbackUrl);
        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(9)
    @DisplayName("【409】查询注册品类（细化申请码类型）")
    public void testM409() {
        String idCodeOfCategory = "";
        String modelNumberCode = "";
        String categoryRegId = "";
        BaseIdCodeInfo result = Four.m409(ConfigReader.MAIN_CODE, idCodeOfCategory, modelNumberCode, categoryRegId);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("主键ID:{} 品类用途编码:{} 品类码号:{} 品类描述:{} 完整码:{} 申请码类型(细化类型):{}", item.getId(), item.getCodeUseId(), item.getCategoryCode(), item.getCategoryMemo(),item.getCompleteCode(),item.getRegistType()));
        }
    }

    @Test
    @Order(10)
    @DisplayName("【410】查询所有注册品类（细化申请码类型）")
    public void testM410() {
        Integer searchType = 1;
        BaseIdCodeInfo result = Four.m410(ConfigReader.MAIN_CODE, searchType);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("主键ID:{} 品类用途编码:{} 品类码号:{} 品类描述:{} 完整码:{}", item.getId(), item.getCodeUseId(), item.getCategoryCode(), item.getCategoryMemo(),item.getCompleteCode()));
        }
    }
}
