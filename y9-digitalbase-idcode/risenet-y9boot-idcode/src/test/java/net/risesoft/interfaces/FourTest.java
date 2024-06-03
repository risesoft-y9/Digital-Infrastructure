package net.risesoft.interfaces;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.enums.CodePayTypeEnum;
import net.risesoft.model.BaseIdCodeInfo;
import net.risesoft.model.BatchRegistInfo;
import net.risesoft.model.BatchRegistResult;
import net.risesoft.model.CategoryRegModel;
import net.risesoft.model.IdcodeRegResult;
import net.risesoft.util.ConfigReader;
import net.risesoft.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@DisplayName("品类注册/备案相关接口")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FourTest {

    @BeforeEach
    public void setUp() {

    }

    /*@Test
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
    }*/

    @Test
    @Order(2)
    @Disabled
    @DisplayName("【402】批量注册/备案品类")
    public void testM402() {
        List<CategoryRegModel> list=new ArrayList<>();
        CategoryRegModel model=new CategoryRegModel();
        model.setCodeUseId(10);
        model.setIndustryCategoryId(10222);
        model.setCategoryCode("46100000");
        model.setModelNumber("单点登录");
        model.setModelNumberCode("sso");
        model.setCategoryMemo("这是品类描述");
        model.setCodePayType(CodePayTypeEnum.REGISTER.getValue());
        model.setGotoUrl("http://example.com/goto");
        model.setSampleUrl("http://example.com/goto");
        //list.add(model);

        CategoryRegModel modelX=new CategoryRegModel();
        modelX.setCodeUseId(73);
        /**
         * 员工/职员名片
         * industryCategoryId: 10127
         * categoryCode: 10000000
         */
        modelX.setIndustryCategoryId(10127);
        modelX.setCategoryCode("10000000");
        modelX.setModelNumber("秦意如");
        modelX.setModelNumberCode("qinyiru");
        modelX.setCategoryMemo("员工/职员名片");
        modelX.setCodePayType(CodePayTypeEnum.REGISTER.getValue());
        modelX.setGotoUrl("http://example.com/goto");
        modelX.setSampleUrl("http://example.com/goto");
        list.add(modelX);

        BatchRegistInfo bri = new BatchRegistInfo();
        bri.setAccessToken(ConfigReader.API_KEY);
        bri.setIdCode(ConfigReader.MAIN_CODE);
        bri.setList(list);
        String jsonStr = JsonUtil.writeValueAsString(bri);
        System.out.println(jsonStr);
        BatchRegistResult result = Four.m402(jsonStr);
        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(3)
    @DisplayName("【403】查询注册品类：通过品类码获取IDcode申请基本信息（精确匹配）")
    public void testM403() {
        String idCodeOfCategory = "46100000";
        String modelNumberCode = "itemAdmin";
        String categoryRegId = "10030000000000001923";
        BaseIdCodeInfo result = Four.m403(ConfigReader.MAIN_CODE, idCodeOfCategory, modelNumberCode, categoryRegId);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("主键ID:{} 品类用途编码:{} 品类码号:{} 品类描述:{} 完整码:{} 型号码:{}", item.getId(), item.getCodeUseId(), item.getCategoryCode(), item.getCategoryMemo(),item.getCompleteCode(), item.getModelNumberCode()));
        }
    }

    @Test
    @Order(4)
    @DisplayName("【404】查询所有注册品类")
    public void testM404() {
        Integer searchType = 1;
        BaseIdCodeInfo result = Four.m404(ConfigReader.MAIN_CODE, searchType);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("主键ID:{} 品类用途编码:{} 品类码号:{} 品类描述:{} 完整码:{}", item.getId(), item.getCodeUseId(), item.getCategoryCode(), item.getCategoryMemo(),item.getCompleteCode()));
        }
    }

    /*@Test
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
    }*/

    @Test
    @Order(6)
    @Disabled
    @DisplayName("【406】注册/备案产品品类IDcode码")
    public void testM406() {
        /**
         * 产品
         * codeUseId: 10 {@link net.risesoft.interfaces.TwoTest#testM201()}
         */
        String codeUseId = "10";
        /**
         * 网站
         * industryCategoryId: 10222
         * categoryCode: 46100000
         */
        Integer industryCategoryId = 1564;
        String categoryCode = "10024006";
        String modelNumber = "电脑桌-001";
        String modelNumberCode = "desktop-001";
        Integer codePayType = CodePayTypeEnum.REGISTER.getValue();
        String gotoUrl = "http://example.com/goto";
        String sampleUrl = "http://example.com/goto";
        IdcodeRegResult result = Four.m406(ConfigReader.MAIN_CODE, codeUseId, industryCategoryId, categoryCode, modelNumber, modelNumberCode, codePayType, gotoUrl, sampleUrl);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("注册/备案结果:{}", result);
        }
    }

    @Test
    @Order(7)
    @Disabled
    @DisplayName("【407】注册/备案非产品品类IDcode码")
    public void testM407() {
        /**
         * 有形资产用途ID
         * codeUseId: 60 {@link net.risesoft.interfaces.TwoTest#testM201()}
         */
        String codeUseId = "60";
        Integer codePayType = CodePayTypeEnum.REGISTER.getValue();
        /**
         * 有形资产
         * 品类ID-industryCategoryId: 10166
         * 品类码号-categoryCode: 10000000
         */
        Integer industryCategoryId = 10166;
        String categoryCode = "10000000";
        String modelNumber = "电脑桌-A002";
        String modelNumberEn = "desktop-A002";
        String introduction = "所属南方第六事业部";
        String gotoUrl = "http://example.com/goto";
        String sampleUrl = "http://example.com/goto";
        IdcodeRegResult result = Four.m407(ConfigReader.MAIN_CODE, codeUseId, industryCategoryId, categoryCode, modelNumber, modelNumberEn, introduction, codePayType, gotoUrl, sampleUrl);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("注册/备案结果:{}", result);
        }
    }

    /*@Test
    @Order(8)
    @Disabled
    @DisplayName("【408】备案品类接口(页面接口) ")
    public void testM408() {
        String gotoUrl = "http://example.com/goto";
        String sampleUrl = "http://example.com/sample";
        String callbackUrl = "http://example.com/callback";
        Result result = Four.m408(ConfigReader.MAIN_CODE, gotoUrl, sampleUrl, callbackUrl);
        assertEquals(result.getResultCode(), 1);
    }*/

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
