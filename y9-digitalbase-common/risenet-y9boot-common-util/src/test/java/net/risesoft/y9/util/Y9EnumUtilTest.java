package net.risesoft.y9.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.risesoft.enums.ValuedEnum;

/**
 * Y9EnumUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/18
 */
public class Y9EnumUtilTest {

    enum TestStringEnum implements ValuedEnum<String> {
        VALUE_ONE("ONE"),
        VALUE_TWO("TWO");

        private final String value;

        TestStringEnum(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    enum TestIntegerEnum implements ValuedEnum<Integer> {
        VALUE_TEN(10),
        VALUE_TWENTY(20);

        private final Integer value;

        TestIntegerEnum(Integer value) {
            this.value = value;
        }

        @Override
        public Integer getValue() {
            return value;
        }
    }

    @Test
    public void testValueOfStringEnum() {
        TestStringEnum result = Y9EnumUtil.valueOf(TestStringEnum.class, "ONE");
        assertEquals(TestStringEnum.VALUE_ONE, result);

        result = Y9EnumUtil.valueOf(TestStringEnum.class, "TWO");
        assertEquals(TestStringEnum.VALUE_TWO, result);
    }

    @Test
    public void testValueOfIntegerEnum() {
        TestIntegerEnum result = Y9EnumUtil.valueOf(TestIntegerEnum.class, 10);
        assertEquals(TestIntegerEnum.VALUE_TEN, result);

        result = Y9EnumUtil.valueOf(TestIntegerEnum.class, 20);
        assertEquals(TestIntegerEnum.VALUE_TWENTY, result);
    }

    @Test
    public void testValueOfWithInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            Y9EnumUtil.valueOf(TestStringEnum.class, "INVALID");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Y9EnumUtil.valueOf(TestIntegerEnum.class, 99);
        });
    }
}