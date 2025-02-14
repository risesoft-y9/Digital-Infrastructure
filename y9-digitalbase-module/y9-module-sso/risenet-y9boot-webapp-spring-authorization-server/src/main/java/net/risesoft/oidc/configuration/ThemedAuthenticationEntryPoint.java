package net.risesoft.oidc.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThemedAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final Map<String, AuthenticationEntryPoint> authenticationEntryPoints;
    private final AuthenticationEntryPoint defaultEntryPoint = new LoginUrlAuthenticationEntryPoint("/login");

    public ThemedAuthenticationEntryPoint() {
        Map<String, AuthenticationEntryPoint> map = new HashMap<>();
        map.put("clientid_oidc", new LoginUrlAuthenticationEntryPoint("/login"));

        this.authenticationEntryPoints = Collections.unmodifiableMap(map);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        AuthenticationEntryPoint delegate = this.defaultEntryPoint;
        
        /*HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String clientId1 = req.getParameter("client_id");
        var savedReq = new HttpSessionRequestCache().getRequest(request, response);
        String clientId2 = savedReq.getParameterValues("client_id")[0];*/

        // Attempt to resolve a specific login url based on clientId
        String clientId = request.getParameter("client_id");
        if (clientId != null) {
            delegate = this.authenticationEntryPoints.getOrDefault(clientId, this.defaultEntryPoint);
        }

        delegate.commence(request, response, authException);
    }
}