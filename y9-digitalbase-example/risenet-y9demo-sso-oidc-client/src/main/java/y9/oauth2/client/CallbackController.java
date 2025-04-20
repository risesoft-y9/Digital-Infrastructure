package y9.oauth2.client;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifier.BaseVerification;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.enums.platform.SexEnum;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.util.Y9EnumUtil;
import y9.oauth2.client.service.CasOidcServiceProxy;
import y9.oauth2.client.service.OpenIdOAuth2AccessToken;

@Controller
@RequestMapping("/public")
@Slf4j
public class CallbackController {

    @Autowired
    private CasOidcServiceProxy serviceProxy;

    @Autowired
    private Environment env;

    @RequestMapping("/oauth/callback")
    public String loginCallback(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String originalUri = (String)session.getAttribute("originalUri");
        String code = request.getParameter("code");
        if (code != null) {
            UserInfo userInfo = null;
            OpenIdOAuth2AccessToken token = serviceProxy.getAccessToken(code);
            if (token != null) {
                String jwtId = "";
                String refreshToken = token.getRefreshToken();
                String accessToken = token.getAccessToken();
                String idToken = token.getOpenIdToken();
                if (isJwtAccessToken(accessToken)) {
                    DecodedJWT jwt = JWT.decode(accessToken);
                    jwtId = jwt.getId();
                    if (verify(jwt)) {
                        userInfo = toUserInfo(jwt);
                    }
                } else {
                    jwtId = accessToken;
                    DecodedJWT jwt = JWT.decode(idToken);
                    if (verify(jwt)) {
                        userInfo = toUserInfo(jwt);
                    }
                }
                session.setAttribute("refreshToken", refreshToken);
                session.setAttribute("accessToken", accessToken);
                session.setAttribute("idToken", idToken);
                session.setAttribute("jwtId", jwtId);
                session.setAttribute("userInfo", userInfo);
                session.setAttribute("loginName", userInfo.getLoginName());
            }
        } else {
            System.out.println("sessionid === " + session.getId());
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                String value = request.getParameter(name);
                System.out.println(name + " === " + value);
            }

            String logoutToken = request.getParameter("logout_token");
            if (logoutToken != null) {
                DecodedJWT jwt = JWT.decode(logoutToken);
                String oidcSessionId = jwt.getClaim("sid").asString();
                System.out.println("oidcSessionId === " + oidcSessionId);
                
                session.invalidate();
                try {
                    request.logout();
                } catch (ServletException e) {
                     e.printStackTrace();
                }
            }
        }
        return "redirect:" + originalUri;
    }

    private boolean isJwtAccessToken(String accessToken) {
        return StringUtils.isNotBlank(accessToken) && accessToken.split("\\.").length == 3;
    }

    private boolean verify(DecodedJWT jwt) {
        String kid = jwt.getKeyId();
        URL url = null;
        try {
            url = URI.create(env.getProperty("y9.feature.oauth2.client.jwks-uri")).toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
        JwkProvider provider = new JwkProviderBuilder(url).cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES).build();

        Jwk jwk = null;
        try {
            jwk = provider.get(kid);
        } catch (JwkException e) {
            e.printStackTrace();
            return false;
        }

        PublicKey publicKey = null;
        try {
            publicKey = jwk.getPublicKey();
        } catch (InvalidPublicKeyException e) {
            e.printStackTrace();
            return false;
        }

        Algorithm algorithm = null;
        switch (jwt.getAlgorithm()) {
            case "RS256":
                algorithm = Algorithm.RSA256((RSAPublicKey)publicKey);
                break;
            case "RS512":
                algorithm = Algorithm.RSA512((RSAPublicKey)publicKey);
        }
        try {
            algorithm.verify(jwt);
        } catch (SignatureVerificationException exception) {
            exception.printStackTrace();
            return false;
        }

        BaseVerification verification = (BaseVerification)JWT.require(algorithm);
        verification.withClaimPresence("tenantId");
        JWTVerifier verifier = verification.build();
        try {
            verifier.verify(jwt);
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    private UserInfo toUserInfo(DecodedJWT jwt) {
        UserInfo userInfo = new UserInfo();
        userInfo.setCaid(jwt.getClaim("caid").asString());
        userInfo.setEmail(jwt.getClaim("email").asString());
        userInfo.setGuidPath(jwt.getClaim("guidPath").asString());
        userInfo.setLoginName(jwt.getClaim("loginName").asString());
        userInfo.setLoginType(jwt.getClaim("loginType").asString());
        userInfo.setMobile(jwt.getClaim("mobile").asString());
        userInfo.setOriginal(jwt.getClaim("original").asBoolean() == null ? false : jwt.getClaim("original").asBoolean());
        userInfo.setOriginalId(jwt.getClaim("originalId").asString());
        userInfo.setParentId(jwt.getClaim("parentId").asString());
        userInfo.setPersonId(jwt.getClaim("personId").asString());
        userInfo.setPositionId(jwt.getClaim("positionId").asString() == null ? "" : jwt.getClaim("positionId").asString());
        userInfo.setSex(jwt.getClaim("original").asInt() == null ? SexEnum.MALE : Y9EnumUtil.valueOf(SexEnum.class, jwt.getClaim("original").asInt()));
        userInfo.setTenantId(jwt.getClaim("tenantId").asString());
        userInfo.setTenantShortName(jwt.getClaim("tenantShortName").asString());
        userInfo.setTenantName(jwt.getClaim("tenantName").asString());
        userInfo.setPositions(jwt.getClaim("positions").asString());
        return userInfo;
    }
}
