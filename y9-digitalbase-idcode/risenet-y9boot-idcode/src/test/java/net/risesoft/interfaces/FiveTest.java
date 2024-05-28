package net.risesoft.interfaces;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.model.BaseIdCodeInfo;
import net.risesoft.model.CodeRecordResult;
import net.risesoft.model.Result;
import net.risesoft.model.ResultObject;
import net.risesoft.util.ConfigReader;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("一物一码二维码接口")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FiveTest {

    @BeforeEach
    public void setUp() {

    }

    @Test
    @Order(1)
    @Disabled
    @DisplayName("【5011】上传码接口（方式一：上传TXT文件）")
    public void testM5011() {
        String categoryRegId = "category_reg_id";
        File codeFile = new File("path/to/codeFile");
        String generateType = "generate_type";
        Result result = Five.m5011(ConfigReader.MAIN_CODE, categoryRegId, codeFile, generateType);
        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(2)
    @Disabled
    @DisplayName("【5012】上传码接口（方式二：参数列表传递）")
    public void testM5012() {
        String categoryRegId = "category_reg_id";
        String codeListStr = "codeListStr";
        String generateType = "generate_type";
        Result result = Five.m5012(ConfigReader.MAIN_CODE, categoryRegId, codeListStr, generateType);
        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(3)
    @Disabled
    @DisplayName("【5013】上传码接口（方式三：前缀 + 起始号、终止号）")
    public void testM5013() {
        String categoryRegId = "category_reg_id";
        String generateType = "generate_type";
        String prefixStr = "";
        Integer startNum = 1;
        Integer endNum = 2;
        Result result = Five.m5013(ConfigReader.MAIN_CODE, categoryRegId, prefixStr, startNum, endNum, generateType);
        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(4)
    @Disabled
    @DisplayName("【5014】上传码接口（方式四：前缀 + 起始号、终止号、数值长度，数值位数不够规定长度时高位补零）")
    public void testM5014() {
        String categoryRegId = "category_reg_id";
        String generateType = "generate_type";
        String prefixStr = "";
        Integer startNum = 1;
        Integer endNum = 2;
        Integer length = 3;
        Result result = Five.m5014(ConfigReader.MAIN_CODE, categoryRegId, prefixStr, startNum, endNum, length, generateType);
        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(5)
    @DisplayName("【502】按品类查询上传的二维码接口")
    public void testM502() {
        String idCodeOfCategory = "10000000";
        String modelNumberCode = "rise15727338539";
        CodeRecordResult result = Five.m502(ConfigReader.MAIN_CODE, idCodeOfCategory, modelNumberCode);
        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(6)
    @Disabled
    @DisplayName("【503】按上传批次ID查询上传的二维码接口")
    public void testM503() {
        String uploadCodeId = "";
        CodeRecordResult result = Five.m503(ConfigReader.MAIN_CODE, uploadCodeId);
        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(7)
    @Disabled
    @DisplayName("【504】获取一个IDcode码内容文件的下载地址")
    public void testM504() {
        String uploadCodeId = "";
        String password = "";
        String codeType = "";
        BaseIdCodeInfo result = Five.m504(ConfigReader.MAIN_CODE, uploadCodeId, password, codeType);
        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(8)
    @Disabled
    @DisplayName("【505】根据上传批次ID获取IDcode码")
    public void testM505() {
        String uploadCodeId = "";
        String codeType = "";
        ResultObject result = Five.m505(ConfigReader.MAIN_CODE, uploadCodeId, codeType);
        assertEquals(result.getResultCode(), 1);
    }

    @Test
    @Order(9)
    @Disabled
    @DisplayName("【506】根据原码查询简码")
    public void testM506() {
        String code = "MA.156.1003.1629/10.46100000.riseplatform";
        ResultObject result = Five.m506(code);
        assertEquals(result.getResultCode(), 1);
    }
}
