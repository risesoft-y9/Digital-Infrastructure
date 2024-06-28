package org.apereo.cas.support.oauth.web.views;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apereo.cas.configuration.model.support.oauth.OAuthCoreProperties;
import org.apereo.cas.configuration.model.support.oauth.OAuthProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.oauth.util.OAuth20Utils;
import org.apereo.cas.ticket.accesstoken.OAuth20AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * This is {@link OAuth20DefaultUserProfileViewRenderer}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
@RequiredArgsConstructor
public class OAuth20DefaultUserProfileViewRenderer implements OAuth20UserProfileViewRenderer {
    protected final ServicesManager servicesManager;

    private final OAuthProperties oauthProperties;

    @Override
    public ResponseEntity render(final Map<String, Object> model,
                                 final OAuth20AccessToken accessToken,
                                 final HttpServletResponse response) {
        val userProfile = getRenderedUserProfile(model, accessToken, response);
        return renderProfileForModel(userProfile, accessToken, response);
    }

    protected ResponseEntity renderProfileForModel(final Map<String, Object> userProfile,
                                                   final OAuth20AccessToken accessToken,
                                                   final HttpServletResponse response) {
        val json = OAuth20Utils.toJson(userProfile); // y9 add 转json时对只有单个属性的数组直接解开，为了兼容旧版本
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    protected Map<String, Object> getRenderedUserProfile(final Map<String, Object> model,
                                                         final OAuth20AccessToken accessToken,
                                                         final HttpServletResponse response) {
        val type = determineUserProfileType(accessToken);
        LOGGER.debug("User profile view type for client [{}] is set to [{}]", accessToken.getClientId(), type);
        if (type == OAuthCoreProperties.UserProfileViewTypes.FLAT) {
            return flattenUserProfile(model);
        }
        return model;
    }

    protected OAuthCoreProperties.UserProfileViewTypes determineUserProfileType(final OAuth20AccessToken accessToken) {
        val registeredService = OAuth20Utils.getRegisteredOAuthServiceByClientId(servicesManager, accessToken.getClientId());
        return registeredService != null && registeredService.getUserProfileViewType() != null
            ? registeredService.getUserProfileViewType()
            : oauthProperties.getCore().getUserProfileViewType();
    }

    protected Map<String, Object> flattenUserProfile(final Map<String, Object> model) {
        val flattened = new LinkedHashMap<String, Object>();
        if (model.containsKey(MODEL_ATTRIBUTE_ATTRIBUTES)) {
            val attributes = (Map) model.get(MODEL_ATTRIBUTE_ATTRIBUTES);
            flattened.putAll(attributes);
        }
        model.keySet()
            .stream()
            .filter(attributeName -> !attributeName.equalsIgnoreCase(MODEL_ATTRIBUTE_ATTRIBUTES))
            .forEach(attributeName -> flattened.put(attributeName, model.get(attributeName)));
        LOGGER.trace("Flattened user profile attributes with the final model as [{}]", model);
        return flattened;
    }
}
