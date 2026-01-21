package net.risesoft.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import net.risesoft.y9public.entity.resource.Y9ResourceBase;

/**
 * Y9ResourceUtil 单元测试
 *
 * @author shidaobang
 * @date 2025/12/18
 */
class Y9ResourceUtilTest {

    /**
     * 测试用的资源实体类
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    private static class TestResource extends Y9ResourceBase {
        private static final long serialVersionUID = 1L;

        private String appId;
        private String parentId;

        @Override
        public String getAppId() {
            return appId;
        }

        @Override
        public String getParentId() {
            return parentId;
        }
    }

    @Test
    void testIsInheritanceChanged_withNullValues() {
        // 准备测试数据
        TestResource originResource = new TestResource();
        TestResource updateResource = new TestResource();

        // 两个资源的inherit都为null的情况
        originResource.setInherit(null);
        updateResource.setInherit(null);
        assertFalse(Y9ResourceUtil.isInheritanceChanged(originResource, updateResource), "两个资源inherit都为null时应该返回false");

        // 一个为null，另一个为false的情况
        originResource.setInherit(null);
        updateResource.setInherit(Boolean.FALSE);
        assertTrue(Y9ResourceUtil.isInheritanceChanged(originResource, updateResource), "null和false应该被认为是不同的");

        // 一个为null，另一个为true的情况
        originResource.setInherit(null);
        updateResource.setInherit(Boolean.TRUE);
        assertTrue(Y9ResourceUtil.isInheritanceChanged(originResource, updateResource), "null和true应该被认为是不同的");
    }

    @Test
    void testIsInheritanceChanged_withSameValues() {
        // 准备测试数据
        TestResource originResource = new TestResource();
        TestResource updateResource = new TestResource();

        // 两个资源的inherit都为false的情况
        originResource.setInherit(Boolean.FALSE);
        updateResource.setInherit(Boolean.FALSE);
        assertFalse(Y9ResourceUtil.isInheritanceChanged(originResource, updateResource),
            "两个资源inherit都为false时应该返回false");

        // 两个资源的inherit都为true的情况
        originResource.setInherit(Boolean.TRUE);
        updateResource.setInherit(Boolean.TRUE);
        assertFalse(Y9ResourceUtil.isInheritanceChanged(originResource, updateResource), "两个资源inherit都为true时应该返回false");
    }

    @Test
    void testIsInheritanceChanged_withDifferentValues() {
        // 准备测试数据
        TestResource originResource = new TestResource();
        TestResource updateResource = new TestResource();

        // origin为false，update为true的情况
        originResource.setInherit(Boolean.FALSE);
        updateResource.setInherit(Boolean.TRUE);
        assertTrue(Y9ResourceUtil.isInheritanceChanged(originResource, updateResource),
            "origin为false，update为true时应该返回true");

        // origin为true，update为false的情况
        originResource.setInherit(Boolean.TRUE);
        updateResource.setInherit(Boolean.FALSE);
        assertTrue(Y9ResourceUtil.isInheritanceChanged(originResource, updateResource),
            "origin为true，update为false时应该返回true");
    }
}