package y9.support;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.risesoft.pojo.Y9Result;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * feign client 错误返回（非2xx段）解析器
 *
 * @author shidaobang
 * @date 2023/05/31
 * @since 9.6.2
 */
public class Y9ErrorDecoder extends ErrorDecoder.Default {
    private final ObjectMapper objectMapper;

    public Y9ErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        if (HttpStatus.BAD_REQUEST.value() == response.status()) {
            try {
                Reader reader = response.body().asReader(Charset.defaultCharset());
                Y9Result<?> y9Result = objectMapper.readValue(reader, Y9Result.class);
                return new Y9ApiException(y9Result.getCode(), y9Result.getMsg());
            } catch (IOException ignored) {
            }
        }
        return super.decode(methodKey, response);
    }
}
