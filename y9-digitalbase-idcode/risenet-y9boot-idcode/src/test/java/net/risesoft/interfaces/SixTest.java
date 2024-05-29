package net.risesoft.interfaces;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.enums.CodeTypeEnum;
import net.risesoft.enums.ColorTypeEnum;
import net.risesoft.enums.MarginTypeEnum;
import net.risesoft.model.CodeAddress;
import net.risesoft.model.CodePicBase64;
import net.risesoft.util.ConfigReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@DisplayName("生成码图接口")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SixTest {

    @Test
    @Order(1)
    @DisplayName("【603】生成码图接口")
    public void testM603() {
        String code = ConfigReader.MAIN_CODE;
        Integer picSize = 300;
        Integer codeType = CodeTypeEnum.QR.getValue();
        CodeAddress result = Six.m603(code, picSize, codeType);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("码图地址:{}", result.getAddress());
        }
    }

    @Test
    @Order(2)
    @DisplayName("【604】生成带边框的码图")
    public void testM604() {
        String code = ConfigReader.ANALYZE_URL + "?code=" + ConfigReader.MAIN_CODE;
        Integer isMargin = 1;
        String unitIcon = "R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";
        Integer qrCodeSize = 400;
        Integer color = ColorTypeEnum.COLOR.getValue();
        CodePicBase64 result = Six.m604(code, isMargin, unitIcon, qrCodeSize, color);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("生成的码图:{}", result.getStr());
        }
    }

    @Test
    @Order(3)
    @DisplayName("【605】生成质量认证二维码图")
    public void testM605() {
        String idCode = ConfigReader.MAIN_CODE;
        String code = ConfigReader.MAIN_CODE;
        Integer useLogo = 1;
        String unitLogo = "R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";
        Integer marginType = MarginTypeEnum.SQUARE.getValue();
        Integer categoryId = 1;
        Integer marginTypeLv2 = 1;
        Integer codeType = CodeTypeEnum.QR.getValue();
        Integer codeSize = 5;
        String codeColor = "000000";
        CodePicBase64 result = Six.m605(idCode, code, useLogo, unitLogo, marginType, categoryId, marginTypeLv2, codeType, codeSize, codeColor);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("生成的码图:{}", result.getStr());
        }
    }
}
