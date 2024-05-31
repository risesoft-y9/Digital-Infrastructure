package net.risesoft.interfaces;

import net.risesoft.consts.UrlConst;
import net.risesoft.model.AreaInfoResult;
import net.risesoft.model.TradeInfoResult;
import net.risesoft.model.UnitTypeInfoResult;
import net.risesoft.util.ConfigReader;
import net.risesoft.util.IDCodeApiExecute;

import java.util.HashMap;
import java.util.Map;

public class One {

    /**
     * 一次性返回所有级别行政信息
     * @return {@link net.risesoft.model.AreaInfoResult}
     */
    public static AreaInfoResult m101() {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        AreaInfoResult result = null;
        try {
            result = new IDCodeApiExecute<AreaInfoResult>().execute(AreaInfoResult.class, params, UrlConst.URL_101, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按照父级ID返回子级信息
     * @param parentId 区域父级ID(必填)，父级ID值为0，则返回第一级区域列表。
     * @param level 需要返回的区域等级（1、2、3）
     * @return {@link net.risesoft.model.AreaInfoResult}
     */
    public static AreaInfoResult m102(Integer parentId, Integer level) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("address_id_parent", parentId);
        params.put("level", level);
        AreaInfoResult result = null;
        try {
            result = new IDCodeApiExecute<AreaInfoResult>().execute(AreaInfoResult.class, params, UrlConst.URL_102, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 一次性返回所有级别行业信息
     * @return {@link net.risesoft.model.TradeInfoResult}
     */
    public static TradeInfoResult m103() {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        TradeInfoResult result = null;
        try {
            result = new IDCodeApiExecute<TradeInfoResult>()
                    .execute(TradeInfoResult.class, params, UrlConst.URL_103, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按照父级ID返回子级信息
     * @param parentId 行业父节点(必填)，父级ID为0时，返回第一级行业信息。
     * @return {@link net.risesoft.model.TradeInfoResult}
     */
    public static TradeInfoResult m104(Integer parentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("trade_id_parent", parentId);
        TradeInfoResult result = null;
        try {
            result = new IDCodeApiExecute<TradeInfoResult>().execute(TradeInfoResult.class, params, UrlConst.URL_104, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取单位性质分类接口
     * @return
     */
    public static UnitTypeInfoResult m105() {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        UnitTypeInfoResult result = null;
        try {
            result = new IDCodeApiExecute<UnitTypeInfoResult>().execute(UnitTypeInfoResult.class, params, UrlConst.URL_105, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
