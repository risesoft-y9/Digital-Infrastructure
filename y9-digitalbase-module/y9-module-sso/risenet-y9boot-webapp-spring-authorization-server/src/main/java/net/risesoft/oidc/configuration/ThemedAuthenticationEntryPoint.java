package net.risesoft.oidc.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.risesoft.oidc.y9.entity.Y9Theme;
import net.risesoft.oidc.y9.repository.Y9ThemeRepository;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ThemedAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final AuthenticationEntryPoint defaultEntryPoint = new LoginUrlAuthenticationEntryPoint("/login");
    private final Y9ThemeRepository y9ThemeRepository;

    private Map<String, AuthenticationEntryPoint> authenticationEntryPoints = new HashMap<>();

    public ThemedAuthenticationEntryPoint(Y9ThemeRepository y9ThemeRepository) {
        this.y9ThemeRepository = y9ThemeRepository;
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
            Optional<Y9Theme> y9Theme = y9ThemeRepository.findById(clientId);
            if (y9Theme.isPresent()) {
                String theme = y9Theme.get().getTheme();
                if (StringUtils.hasText(theme)) {
                    delegate = this.authenticationEntryPoints.get(clientId);
                    if (delegate == null) {
                        delegate = new LoginUrlAuthenticationEntryPoint("/login?theme=" + theme);
                        this.authenticationEntryPoints.put(clientId, delegate);
                    }
                }
            }
        }
        delegate.commence(request, response, authException);
    }
}