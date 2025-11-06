package net.risesoft.interfaces;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.IdCode;
import net.risesoft.model.CodeUseInfoResult;
import net.risesoft.model.IndustryCategoryResult;

@SpringBootTest
@Slf4j
@DisplayName("品类分类相关基础数据接口")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TwoTest {

    @Autowired
    private Environment environment;

    @BeforeEach
    public void setUp() {
        IdCode.init(environment.getProperty("idCode.api_code"), environment.getProperty("idCode.api_key"),
            environment.getProperty("idCode.idCode_url"), environment.getProperty("idCode.main_code"),
            environment.getProperty("idCode.analyze_url"), environment.getProperty("idCode.goto_url"),
            environment.getProperty("idCode.sample_url"));
    }

    @Test
    @Order(1)
    @DisplayName("【201】获取人、事、物所有用途")
    void testM201() {
        CodeUseInfoResult result = Two.m201();
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList()
                .forEach(item -> LOGGER.debug("用途ID:{} 用途名称:{} 用途代码:{} 人事物类型:{}", item.getId(), item.getName(),
                    item.getCode(), item.getTypeId()));
        }
    }

    @Test
    @Order(2)
    @DisplayName("【202】获取所有品类")
    void testM202() {
        IndustryCategoryResult result = Two.m202();
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList()
                .forEach(item -> LOGGER.debug("品类ID:{} 名称:{} 用途:{} 代码:{} ", item.getId(), item.getName(),
                    item.getUseId(), item.getCode()));
        }
    }

    @Test
    @Order(3)
    @DisplayName("【203】获取某一级品类")
    void testM203() {
        /**
         * 为0时获取到第一级品类
         */
        IndustryCategoryResult result = Two.m203(16);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList()
                .forEach(item -> LOGGER.debug("品类名称:{} 品类ID:{} 品类父ID:{} 品类级别:{}", item.getName(), item.getId(),
                    item.getParentId(), item.getLevel()));
        }
    }

    @Test
    @Order(4)
    @DisplayName("【204】获取产品所有品类")
    void testM204() {
        IndustryCategoryResult result = Two.m204();
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList()
                .forEach(item -> LOGGER.debug("品类名称:{} 品类ID:{} 品类父ID:{} 品类级别:{}", item.getName(), item.getId(),
                    item.getParentId(), item.getLevel()));
        }
    }
}
