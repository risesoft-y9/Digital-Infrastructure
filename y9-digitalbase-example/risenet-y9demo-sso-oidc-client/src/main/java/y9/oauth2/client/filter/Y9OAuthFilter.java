package y9.oauth2.client.filter;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import y9.oauth2.client.service.CasOidcServiceProxy;

public class Y9OAuthFilter implements Filter {
	private String clientId = "";
	private String clientSecret = "";
	private RestTemplate restTemplate = new RestTemplate();
	private CasOidcServiceProxy casOAuthServiceProxy;
	private Environment env;
	
	public Y9OAuthFilter(Environment env) {
		this.env = env;
	}

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		clientId = env.getProperty("y9.feature.oauth2.client.clientId");
		clientSecret = env.getProperty("y9.feature.oauth2.client.clientSecret");
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		if (casOAuthServiceProxy == null) {
			casOAuthServiceProxy = Y9Context.getBean(CasOidcServiceProxy.class);
		}

		String uri = request.getRequestURI();
		if (!uri.contains("/public/oauth/callback")) {
			HttpSession session = request.getSession(false);
			if (session == null) {
				auth(request, response);
				return;
			} else {
				String accessToken = (String) session.getAttribute("accessToken");
				ResponseEntity<TokenResponse> introspectEntity = invokeIntrospectEndpoint(accessToken);
				TokenResponse introspectionResponse = introspectEntity.getBody();
				if (!introspectionResponse.isActive()) {
					auth(request, response);
					return;
				}

				UserInfo userInfo = null;
				if (introspectEntity.getStatusCode().is2xxSuccessful()) {
					String attr = null;
					try {
						attr = introspectionResponse.getAttr();
						userInfo = Y9JsonUtil.readValue(attr, UserInfo.class);
					} catch (Exception e) {
						ResponseEntity<String> profileEntity = invokeProfileEndpoint(accessToken);
						String profile = profileEntity.getBody();
						profile = profile.replace("[]", "\"\"");
						userInfo = Y9JsonUtil.readValue(profile, UserInfo.class);
					}
				}

				if (ObjectUtils.isEmpty(userInfo)) {
					auth(request, response);
					return;
				} else {
					Y9LoginUserHolder.setUserInfo(userInfo);
					session.setAttribute("userInfo", userInfo);
					session.setAttribute("loginName", userInfo.getLoginName());
				}
			}
		}

		chain.doFilter(servletRequest, servletResponse);
	}

	/**
	 * 跳转到认证系统
	 *
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void auth(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		if (contextPath != null && contextPath.length() > 0) {
			uri = uri.substring(contextPath.length());
		}
		String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
		String originalUri = uri + queryString;

		request.getSession().setAttribute("originalUri", originalUri);
		String oathUri = casOAuthServiceProxy.getAuthorizationUrl();
		response.sendRedirect(oathUri);
	}

	private ResponseEntity<TokenResponse> invokeIntrospectEndpoint(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBasicAuth(this.clientId, this.clientSecret, StandardCharsets.UTF_8);

		String introspectionUri = env.getProperty("y9.feature.oauth2.client.introspection-uri");
		URI uri = URI.create(introspectionUri + "?token=" + accessToken);
		RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.POST, uri);
		ResponseEntity<TokenResponse> responseEntity = this.restTemplate.exchange(requestEntity, TokenResponse.class);
		return responseEntity;
	}

	private ResponseEntity<String> invokeProfileEndpoint(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Bearer " + accessToken);

		String profileUri = env.getProperty("y9.feature.oauth2.client.profile-uri");
		URI uri = URI.create(profileUri + "?access_token=" + accessToken);
		RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
		ResponseEntity<String> responseEntity = this.restTemplate.exchange(requestEntity, String.class);
		return responseEntity;
	}

}