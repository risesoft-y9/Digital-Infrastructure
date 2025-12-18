package net.risesoft.y9.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * Y9AssertUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/18
 */
class Y9AssertUtilTest {

    @Test
    void testLessThanOrEqualTo() {
        // 测试正常情况，currentValue <= maxValue，不应抛出异常
        assertDoesNotThrow(() -> Y9AssertUtil.lessThanOrEqualTo(5, 10, GlobalErrorCodeEnum.INVALID_ARGUMENT));
        assertDoesNotThrow(() -> Y9AssertUtil.lessThanOrEqualTo(10, 10, GlobalErrorCodeEnum.INVALID_ARGUMENT));

        // 测试异常情况，currentValue > maxValue，应抛出Y9BusinessException异常
        Y9BusinessException exception = assertThrows(Y9BusinessException.class,
            () -> Y9AssertUtil.lessThanOrEqualTo(15, 10, GlobalErrorCodeEnum.INVALID_ARGUMENT));
        assertEquals(GlobalErrorCodeEnum.INVALID_ARGUMENT.getCode(), exception.getCode());
    }

    @Test
    void testNotNull() {
        // 测试正常情况，object不为null，不应抛出异常
        assertDoesNotThrow(() -> Y9AssertUtil.notNull("not null object", GlobalErrorCodeEnum.INVALID_ARGUMENT));

        // 测试异常情况，object为null，应抛出Y9NotFoundException异常
        Y9NotFoundException exception = assertThrows(Y9NotFoundException.class,
            () -> Y9AssertUtil.notNull(null, GlobalErrorCodeEnum.INVALID_ARGUMENT));
        assertEquals(GlobalErrorCodeEnum.INVALID_ARGUMENT.getCode(), exception.getCode());
    }

    @Test
    void testIsNull() {
        // 测试正常情况，object为null，不应抛出异常
        assertDoesNotThrow(() -> Y9AssertUtil.isNull(null, GlobalErrorCodeEnum.INVALID_ARGUMENT));

        // 测试异常情况，object不为null，应抛出Y9BusinessException异常
        Y9BusinessException exception = assertThrows(Y9BusinessException.class,
            () -> Y9AssertUtil.isNull("not null object", GlobalErrorCodeEnum.INVALID_ARGUMENT));
        assertEquals(GlobalErrorCodeEnum.INVALID_ARGUMENT.getCode(), exception.getCode());
    }

    @Test
    void testIsEmpty() {
        // 测试正常情况，list为空，不应抛出异常
        assertDoesNotThrow(() -> Y9AssertUtil.isEmpty(Collections.emptyList(), GlobalErrorCodeEnum.INVALID_ARGUMENT));
        assertDoesNotThrow(() -> Y9AssertUtil.isEmpty(null, GlobalErrorCodeEnum.INVALID_ARGUMENT));

        // 测试异常情况，list不为空，应抛出Y9BusinessException异常
        List<String> list = new ArrayList<>();
        list.add("item");
        Y9BusinessException exception = assertThrows(Y9BusinessException.class,
            () -> Y9AssertUtil.isEmpty(list, GlobalErrorCodeEnum.INVALID_ARGUMENT));
        assertEquals(GlobalErrorCodeEnum.INVALID_ARGUMENT.getCode(), exception.getCode());
    }

    @Test
    void testIsTrue() {
        // 测试正常情况，条件为true，不应抛出异常
        assertDoesNotThrow(() -> Y9AssertUtil.isTrue(true, GlobalErrorCodeEnum.INVALID_ARGUMENT));

        // 测试异常情况，条件为false，应抛出Y9BusinessException异常
        Y9BusinessException exception = assertThrows(Y9BusinessException.class,
            () -> Y9AssertUtil.isTrue(false, GlobalErrorCodeEnum.INVALID_ARGUMENT));
        assertEquals(GlobalErrorCodeEnum.INVALID_ARGUMENT.getCode(), exception.getCode());
    }

    @Test
    void testIsNotTrue() {
        // 测试正常情况，条件为false，不应抛出异常
        assertDoesNotThrow(() -> Y9AssertUtil.isNotTrue(false, GlobalErrorCodeEnum.INVALID_ARGUMENT));

        // 测试异常情况，条件为true，应抛出Y9BusinessException异常
        Y9BusinessException exception = assertThrows(Y9BusinessException.class,
            () -> Y9AssertUtil.isNotTrue(true, GlobalErrorCodeEnum.INVALID_ARGUMENT));
        assertEquals(GlobalErrorCodeEnum.INVALID_ARGUMENT.getCode(), exception.getCode());
    }
}