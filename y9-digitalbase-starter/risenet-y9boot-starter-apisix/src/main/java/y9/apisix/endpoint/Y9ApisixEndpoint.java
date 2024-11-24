package y9.apisix.endpoint;

import java.util.List;

import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import lombok.RequiredArgsConstructor;

import y9.apisix.register.Y9RegisterByApisixRestApi;

@Endpoint(id = "apisix")
@RequiredArgsConstructor
public class Y9ApisixEndpoint {

    private final Y9RegisterByApisixRestApi y9RegisterByApisixRestapi;

    @DeleteOperation
    public List<String> deleteAllRoute() {
        return y9RegisterByApisixRestapi.deleteAllRoute();
    }

    @DeleteOperation
    public List<String> deleteAllUpstream() {
        return y9RegisterByApisixRestapi.deleteAllUpstream();
    }

    @ReadOperation
    public List<String> refresh() {
        return y9RegisterByApisixRestapi.registerApiToApisix();
    }

}
