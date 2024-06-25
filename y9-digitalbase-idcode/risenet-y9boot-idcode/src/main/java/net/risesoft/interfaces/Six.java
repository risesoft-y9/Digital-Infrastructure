package net.risesoft.interfaces;

import java.util.HashMap;
import java.util.Map;

import net.risesoft.consts.UrlConst;
import net.risesoft.model.CodeAddress;
import net.risesoft.model.CodePicBase64;
import net.risesoft.util.ConfigReader;
import net.risesoft.util.IDCodeApiExecute;

public class Six {
    /**
     * 生成码图接口
     *
     * @param code IDcode 码号
     * @param picSize 像素大小(px)
     * @param codeType 码制1：Qr 2：龙贝
     * @return
     */
    public static CodeAddress m603(String code, Integer picSize, Integer codeType) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("code", code);
        params.put("pic_size", picSize);
        params.put("code_type", codeType);
        CodeAddress result = null;
        try {
            result = new IDCodeApiExecute<CodeAddress>().execute(CodeAddress.class, params, UrlConst.URL_603, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成带边框的码图
     *
     * @param code IDcode 码号
     * @param isMargin 是否带边框（必填）0：不带边框 1：带边框
     * @param unitIcon 单位logo（将单位logo图片转换为base64字符串）
     * @param qrCodeSize 像素大小(必填，单位：px)(当is_margin=1时，qrcode_size只能为400、600、800中的一个值)
     * @param color 码图颜色（必填）0：彩色1：黑色
     * @return
     */
    public static CodePicBase64 m604(String code, Integer isMargin, String unitIcon, Integer qrCodeSize,
        Integer color) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("code", code);
        params.put("is_margin", isMargin);
        params.put("unit_icon", unitIcon);
        params.put("qrcode_size", qrCodeSize);
        params.put("color", color);
        CodePicBase64 result = null;
        try {
            result = new IDCodeApiExecute<CodePicBase64>().execute(CodePicBase64.class, params, UrlConst.URL_604, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成质量认证二维码图
     *
     * @param idCode 单位主码（必填）
     * @param code IDcode 码号
     * @param useLogo 是否嵌入单位Logo（必填）0：不嵌入1：嵌入
     * @param unitLogo 单位Logo（将单位Logo图片转换为base64字符串）
     * @param marginType 边框样式（必填）{@link net.risesoft.enums.MarginTypeEnum}
     * @param categoryId 品类ID（当margin_type等于10时必填） 1 产品追溯 2 信息展示 3 内控 4 电子票证
     * @param marginTypeLv2 方形边框二级分类（当margin_type等于20时必填）取值范围：1 - 7；默认值：1
     * @param codeType 码制（必填）{@link net.risesoft.enums.CodeTypeEnum}
     * @param codeSize 码图尺寸(必填，单位：cm，只能为3、5、10中的一个值)
     * @param codeColor 码图颜色值（如：黑色的颜色值为“#000000”，只需要传入“000000”即可；该参数只对QR码有效，其他码制一律为黑色）
     * @return
     */
    public static CodePicBase64 m605(String idCode, String code, Integer useLogo, String unitLogo, Integer marginType,
        Integer categoryId, Integer marginTypeLv2, Integer codeType, Integer codeSize, String codeColor) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", ConfigReader.API_KEY);
        params.put("time", System.currentTimeMillis());
        params.put("company_idcode", idCode);
        params.put("code", code);
        params.put("use_logo", useLogo);
        params.put("unit_logo", unitLogo);
        params.put("margin_type", marginType);
        params.put("category_id", categoryId);
        params.put("margin_type_lv2", marginTypeLv2);
        params.put("code_type", codeType);
        params.put("code_size", codeSize);
        params.put("code_color", codeColor);
        CodePicBase64 result = null;
        try {
            result = new IDCodeApiExecute<CodePicBase64>().execute(CodePicBase64.class, params, UrlConst.URL_605, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}