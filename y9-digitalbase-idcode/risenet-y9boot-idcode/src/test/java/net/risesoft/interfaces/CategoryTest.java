package net.risesoft.interfaces;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.model.CodeUseInfoResult;
import net.risesoft.model.IndustryCategoryResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@DisplayName("品类分类相关基础数据接口")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryTest {

    @Test
    @Order(1)
    @DisplayName("【201】获取人、事、物所有用途接口")
    void testCodeUse() {
        CodeUseInfoResult result = Category.codeUse();
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("用途ID:{} 用途名称:{} 用途代码:{} 人事物类型:{}", item.getId(), item.getName(), item.getCode(), item.getTypeId()));
        }
    }

    @Test
    @Order(2)
    @DisplayName("【202】获取所有品类接口")
    void testIndustryCategory() {
        IndustryCategoryResult result = Category.industryCategory();
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("品类名称:{} 品类ID:{} 品类父ID:{} 品类级别:{}", item.getName(), item.getId(), item.getParentId(), item.getLevel()));
        }
    }

    @Test
    @Order(3)
    @DisplayName("【203】获取某一级品类接口")
    void testIndustryCategoryParentId() {
        /**
         * 为0时获取到第一级品类
         */
        IndustryCategoryResult result = Category.industryCategoryParentId(0);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("品类名称:{} 品类ID:{} 品类父ID:{} 品类级别:{}", item.getName(), item.getId(), item.getParentId(), item.getLevel()));
        }
    }

    @Test
    @Order(4)
    @DisplayName("【204】获取产品所有品类接口")
    void testTradesParentId() {
        IndustryCategoryResult result = Category.industryCategoryProduct();
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("品类名称:{} 品类ID:{} 品类父ID:{} 品类级别:{}", item.getName(), item.getId(), item.getParentId(), item.getLevel()));
        }
    }
}
