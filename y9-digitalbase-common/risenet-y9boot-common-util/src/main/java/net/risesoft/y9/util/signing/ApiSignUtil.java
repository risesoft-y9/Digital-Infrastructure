package net.risesoft.y9.util.signing;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private static final Pattern PARAMETER_SEPARATOR = Pattern.compile("&");

    public static String sign(String appId, String appSecret, String uri, String queryString, String rawBody,
        String timestamp) {
        String sortedQueryString = decodeAndSortQueryString(queryString);
        String stringToSign = appId + uri + sortedQueryString + rawBody + timestamp;
        return computeSignature(appSecret, stringToSign);
    }

    private static String computeSignature(String appSecret, String stringToSign) {
        LOGGER.trace("stringToSign:{}", stringToSign);

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

    private static String decodeAndSortQueryString(String queryString) {
        if (StringUtils.isBlank(queryString)) {
            return "";
        }

        return PARAMETER_SEPARATOR.splitAsStream(queryString)
            .map(ApiSignUtil::parseQueryParameter)
            .sorted((left, right) -> {
                int keyComparison = left.key.compareTo(right.key);
                if (keyComparison != 0) {
                    return keyComparison;
                }
                return StringUtils.compare(left.value, right.value);
            })
            .map(parameter -> parameter.value == null ? parameter.key : parameter.key + "=" + parameter.value)
            .collect(Collectors.joining("&"));
    }

    private static QueryParameter parseQueryParameter(String parameter) {
        int equalsIndex = parameter.indexOf('=');
        if (equalsIndex < 0) {
            return new QueryParameter(URLDecoder.decode(parameter, StandardCharsets.UTF_8), null);
        }

        String key = URLDecoder.decode(parameter.substring(0, equalsIndex), StandardCharsets.UTF_8);
        String value = URLDecoder.decode(parameter.substring(equalsIndex + 1), StandardCharsets.UTF_8);
        return new QueryParameter(key, value);
    }

    private static class QueryParameter {
        private final String key;
        private final String value;

        private QueryParameter(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}
