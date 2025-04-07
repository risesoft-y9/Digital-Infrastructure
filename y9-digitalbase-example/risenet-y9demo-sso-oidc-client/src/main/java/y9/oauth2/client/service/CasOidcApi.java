package y9.oauth2.client.service;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;

import net.risesoft.y9.Y9Context;

public class CasOidcApi extends DefaultApi20 {

	protected CasOidcApi() {
	}

	private static class InstanceHolder {
		private static final CasOidcApi INSTANCE = new CasOidcApi();
	}

	public static CasOidcApi instance() {
		return InstanceHolder.INSTANCE;
	}
	
	@Override
	public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
		return OpenIdJsonTokenExtractor.instance();
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		String url = Y9Context.getProperty("y9.feature.oauth2.client.authorization-uri");
		return url;
	}

	@Override
	public String getAccessTokenEndpoint() {
		String url = Y9Context.getProperty("y9.feature.oauth2.client.accessToken-uri");
		return url;
	}

}
