package net.risesoft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import net.risesoft.enums.CodePayTypeEnum;
import net.risesoft.enums.CodeTypeEnum;
import net.risesoft.interfaces.Four;
import net.risesoft.interfaces.Six;
import net.risesoft.interfaces.Three;
import net.risesoft.model.CodeAddress;
import net.risesoft.model.IdcodeRegResult;
import net.risesoft.model.Result;
import net.risesoft.util.ConfigReader;

@SpringBootTest
class RisenetY9demoIdcodeApplicationTests {

    @Test
    @DisplayName("【306】获取单位基本信息")
    void testM306() {
        Result result = Three.m306(ConfigReader.MAIN_CODE);
        assertEquals(result.getResultCode(), 1);
        System.out.println("结果信息:" + result);
    }

    @Test
    @Disabled
    @DisplayName("【407】注册/备案非产品品类IDcode码")
    public void testM407() {
        /**
         * 有形资产用途ID codeUseId: 60 {@link net.risesoft.interfaces.TwoTest#testM201()}
         */
        String codeUseId = "60";
        Integer codePayType = CodePayTypeEnum.REGISTER.getValue();
        /**
         * 有形资产 品类ID-industryCategoryId: 10166 品类码号-categoryCode: 10000000
         */
        Integer industryCategoryId = 10166;
        String categoryCode = "10000000";
        String modelNumber = "电脑桌-A002";
        String modelNumberEn = "desktop-A002";
        String introduction = "所属南方第六事业部";
        String gotoUrl = "http://example.com/goto";
        String sampleUrl = "http://example.com/goto";
        IdcodeRegResult result = Four.m407(ConfigReader.MAIN_CODE, codeUseId, industryCategoryId, categoryCode,
            modelNumber, modelNumberEn, introduction, codePayType, gotoUrl, sampleUrl);
        assertEquals(result.getResultCode(), 1);
        System.out.println("注册信息:" + result);
    }

    @Test
    @DisplayName("【603】生成码图")
    public void testM603() {
        String code = "MA.156.1003.1629/60.10000000.2/";
        Integer picSize = 300;
        Integer codeType = CodeTypeEnum.QR.getValue();
        CodeAddress result = Six.m603(code, picSize, codeType);
        assertEquals(result.getResultCode(), 1);
        System.out.println("码图地址:" + result.getAddress());
    }
}
