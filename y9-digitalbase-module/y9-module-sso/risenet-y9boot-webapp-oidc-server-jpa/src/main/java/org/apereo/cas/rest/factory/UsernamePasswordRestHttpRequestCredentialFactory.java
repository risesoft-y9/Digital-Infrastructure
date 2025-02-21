package org.apereo.cas.rest.factory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.RememberMeCredential;
import org.apereo.cas.authentication.credential.RememberMeUsernamePasswordCredential;
import org.apereo.cas.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * This is {@link UsernamePasswordRestHttpRequestCredentialFactory}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
@Getter
@Setter
public class UsernamePasswordRestHttpRequestCredentialFactory implements RestHttpRequestCredentialFactory {
	private int order = Integer.MIN_VALUE;

	@Override
	public List<Credential> fromRequest(final HttpServletRequest request,
			final MultiValueMap<String, String> requestBody) {
		// rememberMe screenDimension userAgent clientIp clientMac clientDiskId
		// clientHostName noLoginScreen
		if (requestBody == null || requestBody.isEmpty()) {
			LOGGER.debug("Skipping {} because the requestBody is null or empty", getClass().getSimpleName());
			return new ArrayList<>(0);
		}

		final String username = requestBody.getFirst(RestHttpRequestCredentialFactory.PARAMETER_USERNAME);
		final String password = requestBody.getFirst(RestHttpRequestCredentialFactory.PARAMETER_PASSWORD);
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			LOGGER.debug("Invalid payload; missing required fields.");
			return new ArrayList<>(0);
		}

		final String rememberMe = requestBody.getFirst(RememberMeCredential.REQUEST_PARAMETER_REMEMBER_ME);
		final String tenantShortName = requestBody.getFirst("tenantShortName");
		final String deptId = requestBody.getFirst("deptId");
		final String positionId = requestBody.getFirst("positionId");
		final String loginType = requestBody.getFirst("loginType");
		final String screenDimension = requestBody.getFirst("screenDimension");
		final String systemName = requestBody.getFirst("systemName");

		Map<String, Object> customFields = new LinkedHashMap<>();
		customFields.put("tenantShortName", tenantShortName);
		customFields.put("noLoginScreen", true);
		customFields.put("deptId", deptId);
		customFields.put("positionId", positionId);
		customFields.put("loginType", loginType);
		customFields.put("screenDimension", screenDimension);
		customFields.put("systemName", systemName);

		RememberMeUsernamePasswordCredential credential = new RememberMeUsernamePasswordCredential(
				BooleanUtils.toBoolean(rememberMe));
		credential.setUsername(username);
		credential.assignPassword(password);
		credential.setCustomFields(customFields);
		// prepareCredential(request, credential);
		return CollectionUtils.wrap(credential);
	}
}
