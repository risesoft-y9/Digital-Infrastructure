package net.risesoft.y9.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * RemoteCallUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/18
 */
class RemoteCallUtilTest {

    private static final String TEST_URL = "http://localhost:8080/test";

    private List<NameValuePair> params;

    @BeforeEach
    void setUp() {
        params = new ArrayList<>();
        params.add(new NameValuePair("param1", "value1"));
        params.add(new NameValuePair("param2", "value2"));
    }

    @Test
    void testObjectToNameValuePairList() {
        // 准备测试数据
        TestData testData = new TestData();
        testData.setField1("value1");
        testData.setField2("value2");

        // 执行测试
        List<NameValuePair> result = RemoteCallUtil.objectToNameValuePairList(testData);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(
            result.stream().anyMatch(pair -> "field1".equals(pair.getName()) && "value1".equals(pair.getValue())));
        assertTrue(
            result.stream().anyMatch(pair -> "field2".equals(pair.getName()) && "value2".equals(pair.getValue())));
    }

    // 测试数据类
    static class TestData {
        private String field1;
        private String field2;

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }
    }
}