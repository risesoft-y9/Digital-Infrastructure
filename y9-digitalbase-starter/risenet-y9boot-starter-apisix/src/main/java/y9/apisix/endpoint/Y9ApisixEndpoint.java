package y9.apisix.endpoint;

import java.util.List;

import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import y9.apisix.register.Y9RegisterByApisixRestApi;

@RestControllerEndpoint(id = "apisix")
@RequiredArgsConstructor
public class Y9ApisixEndpoint {

    private final Y9RegisterByApisixRestApi y9RegisterByApisixRestapi;

    @RequestMapping("/deleteAllRoute")
    public List<String> deleteAllRoute() {
        return y9RegisterByApisixRestapi.deleteAllRoute();
    }

    @RequestMapping("/deleteAllUpstream")
    public List<String> deleteAllUpstream() {
        return y9RegisterByApisixRestapi.deleteAllUpstream();
    }

    @RequestMapping("/refresh")
    public List<String> refresh() {
        return y9RegisterByApisixRestapi.registerApiToApisix();
    }

}
