package y9.util.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import lombok.extern.slf4j.Slf4j;

import y9.util.json.Y9JacksonUtil;

/**
 * 云信工具类
 *
 * @author mengjuhua
 * @author qinman
 * @author dingzhaojun
 * @author shidaobang
 *
 * @date 2022/09/26
 */
@Slf4j
public class Y9YunSmsUtil {

    /**
     * 发送验证码的请求路径URL
     */
    private static final String SERVER_URL = "https://api.netease.im/sms/sendcode.action";
    /**
     * 网易云信分配的账号
     */
    private static final String APP_KEY = "ec7694c9e60ddb49b476621792a413c9";
    /**
     * 网易云信分配的密钥
     */
    private static final String APP_SECRET = "c21d1806c39a";
    /**
     * 模板ID
     */
    private static final String MOULD_ID = "3113126";
    /**
     * 随机数
     */
    private static final String NONCE = "123456";
    private static final String CODELEN = "6";

    private static final char[] HEX_DIGITS =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String encode(String algorithm, String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(value.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算并获取checkSum
     */
    public static String getCheckSum(String appSecret, String nonce, String curTime) {
        return encode("SHA", appSecret + nonce + curTime);
    }

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder sb = new StringBuilder(len * 2);
        for (int i = 0; i < len; i++) {
            sb.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            sb.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static String sendYunxin(String mobile, String code) {
        String resualt = null;
        try {
            String curTime = String.valueOf((System.currentTimeMillis() / 1000L));
            String checkSum = Y9YunSmsUtil.getCheckSum(APP_SECRET, NONCE, curTime);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost post = new HttpPost(SERVER_URL);
            post.addHeader("AppKey", APP_KEY);
            post.addHeader("Nonce", NONCE);
            post.addHeader("CurTime", curTime);
            post.addHeader("CheckSum", checkSum);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("templateid", MOULD_ID));
            nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
            nameValuePairs.add(new BasicNameValuePair("codeLen", CODELEN));
            // 自定义验证码
            if (!StringUtils.isEmpty(code)) {
                nameValuePairs.add(new BasicNameValuePair("authCode", code));
            }
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));
            CloseableHttpResponse res = httpclient.execute(post);
            String responseEntity = EntityUtils.toString(res.getEntity(), "utf-8");

            HashMap<String, Object> map = Y9JacksonUtil.readHashMap(responseEntity);
            if (map != null) {
                resualt = map.get("code").toString();
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return resualt;
    }
}
