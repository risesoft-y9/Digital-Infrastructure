package net.risesoft.y9.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Y9BeanUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/18
 */
class Y9BeanUtilTest {

    // 测试用的实体类
    @Data
    @AllArgsConstructor
    public static class Person {
        private String id;
        private String name;
        private String mobile;
        private String sex;
        private String address;
    }

    // 另一个用于测试的实体类，只有部分属性
    @Data
    @AllArgsConstructor
    public static class PartialPerson {
        private String id;
        private String name;
        private String mobile;
    }

    @Test
    void testCopyProperties_basic() {
        // 准备测试数据
        Person source = new Person("123", "Tom", "120", null, null);
        Person target = new Person("123", "Tom", "110", "male", "LuoHu");

        // 执行复制操作
        Y9BeanUtil.copyProperties(source, target);

        // 验证结果
        assertEquals("123", target.getId());
        assertEquals("Tom", target.getName());
        assertEquals("120", target.getMobile()); // 应该从source复制过来
        assertEquals("male", target.getSex()); // source中为null，不应该被复制
        assertEquals("LuoHu", target.getAddress()); // source中为null，不应该被复制
    }

    @Test
    void testCopyProperties_withIgnoreProperties() {
        // 准备测试数据
        Person source = new Person("123", "Tom", "120", "female", "ShenZhen");
        Person target = new Person("123", "Tom", "110", "male", "LuoHu");

        // 执行复制操作，忽略mobile属性
        Y9BeanUtil.copyProperties(source, target, "mobile");

        // 验证结果
        assertEquals("123", target.getId());
        assertEquals("Tom", target.getName());
        assertEquals("110", target.getMobile()); // 被忽略，应该保持原值
        assertEquals("female", target.getSex()); // 应该从source复制过来
        assertEquals("ShenZhen", target.getAddress()); // 应该从source复制过来
    }

    @Test
    void testCopyProperties_withEditable() {
        // 准备测试数据
        Person source = new Person("123", "Tom", "120", "female", "ShenZhen");
        Person target = new Person("123", "Tom", "110", "male", "LuoHu");

        // 执行复制操作，指定可编辑的类
        Y9BeanUtil.copyProperties(source, target, Person.class);

        // 验证结果
        assertEquals("123", target.getId());
        assertEquals("Tom", target.getName());
        assertEquals("120", target.getMobile());
        assertEquals("female", target.getSex());
        assertEquals("ShenZhen", target.getAddress());
    }

    @Test
    void testCopyProperties_partialObject() {
        // 准备测试数据
        PartialPerson source = new PartialPerson("123", "Tom", "120");
        Person target = new Person("123", "Tom", "110", "male", "LuoHu");

        // 执行复制操作
        Y9BeanUtil.copyProperties(source, target);

        // 验证结果
        assertEquals("123", target.getId());
        assertEquals("Tom", target.getName());
        assertEquals("120", target.getMobile()); // 应该从source复制过来
        assertEquals("male", target.getSex()); // source中没有此属性，应该保持原值
        assertEquals("LuoHu", target.getAddress()); // source中没有此属性，应该保持原值
    }

    @Test
    void testCopyProperties_nullSource() {
        Person target = new Person("123", "Tom", "110", "male", "LuoHu");

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            Y9BeanUtil.copyProperties(null, target);
        });
    }

    @Test
    void testCopyProperties_nullTarget() {
        Person source = new Person("123", "Tom", "120", null, null);

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            Y9BeanUtil.copyProperties(source, null);
        });
    }
}