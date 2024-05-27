package net.risesoft.interfaces;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.enums.AddressLevelEnum;
import net.risesoft.model.AreaInfoResult;
import net.risesoft.model.TradeInfoResult;
import net.risesoft.model.UnitTypeInfoResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
@DisplayName("单位属性相关的基础数据接口")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnitAttrTest {

    @Test
    @Order(1)
    @DisplayName("【101】一次性返回所有级别行政信息")
    void testAddresses() {
        AreaInfoResult result = UnitAttr.addresses();
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("区划名称:{} 区域ID:{} 区域父ID:{} 区域级别:{}", item.getName(), item.getId(), item.getParentId(), item.getLevel()));
        }
    }

    @Test
    @Order(2)
    @DisplayName("【102】按照父级ID返回子级信息")
    void testAddressesParentId() {
        /**
         * 440000 代表广东省
         */
        AreaInfoResult result = UnitAttr.addressesParentId(440000, AddressLevelEnum.PREFECTURE.getValue());
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("区划名称:{} 区域ID:{} 区域父ID:{} 区域级别:{}", item.getName(), item.getId(), item.getParentId(), item.getLevel()));
        }
    }

    @Test
    @Order(3)
    @DisplayName("【103】一次性返回所有级别行业信息")
    void testTrades() {
        TradeInfoResult result = UnitAttr.trades();
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("行业名称:{} 行业ID:{} 行业父ID:{} 行业级别:{}", item.getName(), item.getId(), item.getParentId(), item.getLevel()));
        }
    }

    @Test
    @Order(4)
    @DisplayName("【104】按照父级ID返回子级信息")
    void testTradesParentId() {
        /**
         * 农、林、牧、渔业 行业ID为2
         */
        TradeInfoResult result = UnitAttr.tradesParentId(2);
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("行业名称:{} 行业ID:{} 行业父ID:{} 行业级别:{}", item.getName(), item.getId(), item.getParentId(), item.getLevel()));
        }
    }

    @Test
    @Order(5)
    @DisplayName("【105】获取单位性质分类接口")
    void testUnitTypes() {
        UnitTypeInfoResult result = UnitAttr.unitTypes();
        assertEquals(result.getResultCode(), 1);
        if (LOGGER.isDebugEnabled()) {
            result.getList().forEach(item -> LOGGER.debug("单位性质名称:{} 单位性质编码:{}", item.getName(), item.getCode()));
        }
    }
}
