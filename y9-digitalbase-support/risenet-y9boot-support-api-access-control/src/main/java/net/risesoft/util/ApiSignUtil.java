package net.risesoft.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 接口签名 util
 *
 * @author shidaobang
 * @date 2024/11/28
 * @since 9.6.8
 */
@Slf4j
public class ApiSignUtil {

    private final static String ALGORITHM = "HmacSHA256";

    public static String sign(String appId, String appSecret, String uri, String queryString, String rawBody,
        String timestamp) {
        String sortedQueryString = sortAndToString(queryString);
        String stringToSign = appId + uri + sortedQueryString + rawBody + timestamp;
        return sign(appSecret, stringToSign);
    }

    private static String sign(String appSecret, String stringToSign) {
        LOGGER.info("stringToSign:{}", stringToSign);

        Mac mac = null;
        try {
            mac = Mac.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException ignored) {
        }
        try {
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
        } catch (InvalidKeyException ignored) {
        }
        byte[] signatureBytes = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));

        return Hex.encodeHexString(signatureBytes).toUpperCase();
    }

    private static String sortAndToString(String queryString) {
        if (StringUtils.isNotBlank(queryString)) {
            String decodeString = URLDecoder.decode(queryString, StandardCharsets.UTF_8);
            Stream<String> paramStream = Arrays.stream(decodeString.split("&"));
            return paramStream.sorted().collect(Collectors.joining("&"));
        }
        return "";
    }

    public static void main(String[] args) {
        String sign = sign("111", "222", "/api/app/1", "", "", "1732851666087");
        System.out.println(sign);
        sign = sign("222", "111" + "/api/app/1" + "" + "" + "1732851666087");
        System.out.println(sign);
    }

}
