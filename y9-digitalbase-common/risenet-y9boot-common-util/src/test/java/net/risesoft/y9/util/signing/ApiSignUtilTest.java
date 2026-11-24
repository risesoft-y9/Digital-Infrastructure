package net.risesoft.y9.util.signing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ApiSignUtilTest {

    @Test
    void signsRequestsWithoutQueryParameters() {
        String expected = "59438D5F8294314171FF932D56E61FFED52385A6825AD09D5A2050EF439FEFD4";

        assertEquals(expected, ApiSignUtil.sign("test-app", "test-secret-密钥", "/api/items", null, "", "1700000000"));
        assertEquals(expected, ApiSignUtil.sign("test-app", "test-secret-密钥", "/api/items", "", "", "1700000000"));
        assertEquals(expected, ApiSignUtil.sign("test-app", "test-secret-密钥", "/api/items", " \t", "", "1700000000"));
    }

    @Test
    void decodesAndSortsQueryParametersBeforeSigning() {
        String query = "z=%E4%B8%AD%E6%96%87+space&a=second%26%2Bextra&a=first";
        String body = "{\"name\":\"测试\"}";
        String expected = "D8856B512437EAEE20F3FC92A144BDA3E999DC4A72DACF245A5D2006C760A473";

        assertEquals(expected, ApiSignUtil.sign("test-app", "test-secret-密钥", "/api/items", query, body, "1700000000"));
    }

    @Test
    void sortsByKeyThenByValue() {
        String query = "ab=0&a=two&a-b=0&a=z&a=&a=one&a";
        String expected = "4F8928FF99DF10ACE3DB75B3606FDF0B03B0FFA5ABD52720F55BE591BA316974";

        assertEquals(expected, ApiSignUtil.sign("test-app", "test-secret-密钥", "/api/items", query, "", "1700000000"));
    }
}
