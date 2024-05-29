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

    public static AreaInfoResult m101() {
        Map<String, Object> params = new HashMap<String, Object>();
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


    public static AreaInfoResult m102(Integer parentId, Integer level) {
        Map<String, Object> params = new HashMap<String, Object>();
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


    public static TradeInfoResult m103() {
        Map<String, Object> params = new HashMap<String, Object>();
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


    public static TradeInfoResult m104(Integer parentId) {
        Map<String, Object> params = new HashMap<String, Object>();
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


    public static UnitTypeInfoResult m105() {
        Map<String, Object> params = new HashMap<String, Object>();
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
