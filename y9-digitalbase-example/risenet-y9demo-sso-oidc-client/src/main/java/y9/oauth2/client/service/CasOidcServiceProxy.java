package y9.oauth2.client.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.ServiceBuilderOAuth20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

@Service
public class CasOidcServiceProxy {

    @Autowired
    private Environment env;

    private OAuth20Service oauthService;

    @PostConstruct
    public void init() {
        oauthService = createService();
    }

    public OAuth20Service createService() {
        String callbackUrl = env.getProperty("y9.feature.oauth2.client.root-uri") + "/public/oauth/callback";
        String key = env.getProperty("y9.feature.oauth2.client.clientId");
        String secret = env.getProperty("y9.feature.oauth2.client.clientSecret");
        String scope = env.getProperty("y9.feature.oauth2.client.scope");
        String type = env.getProperty("y9.feature.oauth2.client.responseType");
        if (!StringUtils.hasText(type)) {
            type = "code";
        }

        ServiceBuilderOAuth20 sb = new ServiceBuilder(key).defaultScope(scope).responseType(type).callback(callbackUrl);
        sb.apiSecret(secret);
        OAuth20Service oauthService = sb.build(CasOidcApi.instance());
        return oauthService;
    }

    public String getAuthorizationUrl() {
        return oauthService.getAuthorizationUrl();
    }

    public OpenIdOAuth2AccessToken getAccessToken(String code) {
        OpenIdOAuth2AccessToken token = null;
        try {
            token = (OpenIdOAuth2AccessToken)oauthService.getAccessToken(code);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return token;
    }

    public String getProfile(String accessToken) {
        String url = String.format(env.getProperty("y9.feature.oauth2.client.profile-uri"));

        OAuth2AccessToken token = new OAuth2AccessToken(accessToken);
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        oauthService.signRequest(token, request);
        Response response = null;
        try {
            response = oauthService.execute(request);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }

        String responseJson = "";
        if (response != null) {
            try {
                responseJson = response.getBody();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return responseJson;
    }

    public String logout(String idToken) throws IOException, InterruptedException, ExecutionException {
        String root = env.getProperty("y9.feature.oauth2.client.root-uri");
        String callbackUrl = root + "/public/oauth/callback";
        String logout = String.format(env.getProperty("y9.feature.oauth2.client.logout-uri"));

        OAuthRequest request = new OAuthRequest(Verb.GET, logout);
        request.addParameter("post_logout_redirect_uri", callbackUrl);
        request.addParameter("id_token_hint", idToken);
        request.addParameter("state", "random string");
        Response response = oauthService.execute(request);
        String str = response.getBody();
        return str;
    }

}
