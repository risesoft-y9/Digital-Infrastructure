package org.apereo.cas.web.support.gen;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.CoreAuthenticationUtils;
import org.apereo.cas.authentication.RememberMeCredential;
import org.apereo.cas.util.LoggingUtils;
import org.apereo.cas.util.function.FunctionUtils;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.apereo.cas.web.cookie.CookieGenerationContext;
import org.apereo.cas.web.cookie.CookieSameSitePolicy;
import org.apereo.cas.web.cookie.CookieValueManager;
import org.apereo.cas.web.support.InvalidCookieException;
import org.apereo.cas.web.support.WebUtils;
import org.apereo.cas.web.support.mgmr.NoOpCookieValueManager;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.execution.RequestContext;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * Extends CookieGenerator to allow you to retrieve a value from a request. The cookie is automatically marked as httpOnly, if the servlet container has support for it. Also has support for remember-me.
 *
 * @author Scott Battaglia
 * @author Misagh Moayyed
 * @since 3.1
 */
@Slf4j
@Getter
public class CookieRetrievingCookieGenerator extends CookieGenerator implements Serializable, CasCookieBuilder {
    private static final long serialVersionUID = -4926982428809856313L;

    /**
     * Is remember me authentication ?
     *
     * @param requestContext the request context
     * @return true/false
     */
    public static Boolean isRememberMeAuthentication(final RequestContext requestContext) {
        if (isRememberMeProvidedInRequest(requestContext)) {
            LOGGER.debug("This request is from a remember-me authentication event");
            return Boolean.TRUE;
        }
        val authn = WebUtils.getAuthentication(requestContext);
        if (CoreAuthenticationUtils.isRememberMeAuthentication(authn)) {
            LOGGER.debug("The recorded authentication is from a remember-me request");
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private static boolean isRememberMeProvidedInRequest(final RequestContext requestContext) {
        val request = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
        val value = request.getParameter(RememberMeCredential.REQUEST_PARAMETER_REMEMBER_ME);
        LOGGER.trace("Locating request parameter [{}] with value [{}]", RememberMeCredential.REQUEST_PARAMETER_REMEMBER_ME, value);
        return StringUtils.isNotBlank(value) && WebUtils.isRememberMeAuthenticationEnabled(requestContext);
    }

    /**
     * Responsible for manging and verifying the cookie value.
     **/
    private final CookieValueManager casCookieValueManager;

    private final CookieGenerationContext cookieGenerationContext;

    public CookieRetrievingCookieGenerator(final CookieGenerationContext context) {
        this(context, NoOpCookieValueManager.INSTANCE);
    }

    public CookieRetrievingCookieGenerator(final CookieGenerationContext context, final CookieValueManager casCookieValueManager) {
        super.setCookieName(context.getName());
        super.setCookiePath(context.getPath());
        super.setCookieMaxAge(context.getMaxAge());
        super.setCookieSecure(context.isSecure());
        super.setCookieHttpOnly(context.isHttpOnly());
        this.setCookieDomain(context.getDomain());

        this.cookieGenerationContext = context;
        this.casCookieValueManager = casCookieValueManager;
    }

    @Override
    public Cookie addCookie(final HttpServletRequest request, final HttpServletResponse response, final boolean rememberMe, final String cookieValue) {
        val theCookieValue = this.casCookieValueManager.buildCookieValue(cookieValue, request);
        val cookie = createCookie(theCookieValue);

        if (rememberMe) {
            LOGGER.trace("Creating CAS cookie [{}] for remember-me authentication", getCookieName());
            cookie.setMaxAge(cookieGenerationContext.getRememberMeMaxAge());
            cookie.setComment(String.format("%s Remember-Me", cookieGenerationContext.getComment()));
        } else {
            LOGGER.trace("Creating CAS cookie [{}]", getCookieName());
            if (getCookieMaxAge() != null) {
                cookie.setMaxAge(getCookieMaxAge());
            }
        }
        cookie.setSecure(isCookieSecure());
        cookie.setHttpOnly(isCookieHttpOnly());

        return addCookieHeaderToResponse(cookie, request, response);
    }

    @Override
    public Cookie addCookie(final HttpServletRequest request, final HttpServletResponse response, final String cookieValue) {
        return addCookie(request, response, false, cookieValue);
    }

    /**
     * Add cookie header to response.
     *
     * @param cookie the cookie
     * @param request the request
     * @param response the response
     * @return the cookie
     */
    protected Cookie addCookieHeaderToResponse(final Cookie cookie, final HttpServletRequest request, final HttpServletResponse response) {
        val builder = new StringBuilder();
        builder.append(String.format("%s=%s;", cookie.getName(), cookie.getValue()));

        if (cookie.getMaxAge() > -1) {
            builder.append(String.format(" Max-Age=%s;", cookie.getMaxAge()));
        }
        if (StringUtils.isNotBlank(cookie.getDomain())) {
            builder.append(String.format(" Domain=%s;", cookie.getDomain()));
        }
        val path = cleanCookiePath(cookie.getPath());
        builder.append(String.format(" Path=%s;", path));
        val sameSiteResult = CookieSameSitePolicy.of(cookieGenerationContext).build(request, response);
        sameSiteResult.ifPresent(result -> builder.append(String.format(" %s", result)));
        val sameSitePolicy = cookieGenerationContext.getSameSitePolicy().toLowerCase();
        if (cookie.getSecure() || (sameSiteResult.isPresent() && StringUtils.equalsIgnoreCase(sameSiteResult.get(), "none"))) {
            builder.append(" Secure;");
            LOGGER.trace("Marked cookie [{}] as secure as indicated by cookie configuration or " + "the configured same-site policy set to [{}]", cookie.getName(), sameSitePolicy);
        }
        if (cookie.isHttpOnly()) {
            builder.append(" HttpOnly;");
        }
        val value = StringUtils.removeEndIgnoreCase(builder.toString(), ";");
        LOGGER.trace("Adding cookie header as [{}]", value);
        val setCookieHeaders = response.getHeaders("Set-Cookie");
        response.setHeader("Set-Cookie", value);
        setCookieHeaders.stream().filter(header -> !header.startsWith(cookie.getName() + '=')).forEach(header -> response.addHeader("Set-Cookie", header));
        return cookie;
    }

    private String cleanCookiePath(final String givenPath) {
        return FunctionUtils.doIf(StringUtils.isBlank(cookieGenerationContext.getPath()), () -> {
            val path = StringUtils.removeEndIgnoreCase(StringUtils.defaultIfBlank(givenPath, DEFAULT_COOKIE_PATH), "/");
            return StringUtils.defaultIfBlank(path, "/");
        }, () -> givenPath).get();
    }

    @Override
    protected Cookie createCookie(@NonNull final String cookieValue) {
        val c = super.createCookie(cookieValue);
        c.setComment(cookieGenerationContext.getComment());
        c.setPath(cleanCookiePath(c.getPath()));
        return c;
    }

    @Override
    public void removeAll(final HttpServletRequest request, final HttpServletResponse response) {
        Optional.ofNullable(request.getCookies()).ifPresent(cookies -> Arrays.stream(cookies).filter(c -> StringUtils.equalsIgnoreCase(c.getName(), getCookieName())).forEach(c -> Stream.of("/", getCookiePath(), StringUtils.appendIfMissing(getCookiePath(), "/")).forEach(path -> {
            c.setMaxAge(0);
            c.setPath(path);
            c.setSecure(isCookieSecure());
            c.setHttpOnly(isCookieHttpOnly());
            c.setComment(cookieGenerationContext.getComment());
            LOGGER.debug("Removing cookie [{}] with path [{}]", c.getName(), c.getPath());
            response.addCookie(c);
        })));
    }

    @Override
    public String retrieveCookieValue(final HttpServletRequest request) {
        try {
            if (StringUtils.isBlank(getCookieName())) {
                throw new InvalidCookieException("Cookie name is undefined");
            }
            var cookie = org.springframework.web.util.WebUtils.getCookie(request, Objects.requireNonNull(getCookieName()));
            if (cookie == null) {
                val cookieValue = request.getHeader(getCookieName());
                if (StringUtils.isNotBlank(cookieValue)) {
                    LOGGER.trace("Found cookie [{}] under header name [{}]", cookieValue, getCookieName());
                    cookie = createCookie(cookieValue);
                }
            }
            if (cookie == null) {
                val cookieValue = request.getParameter(getCookieName());
                if (StringUtils.isNotBlank(cookieValue)) {
                    LOGGER.trace("Found cookie [{}] under request parameter name [{}]", cookieValue, getCookieName());
                    cookie = createCookie(cookieValue);
                }
            }

            if (cookie == null) {// y9 add start
                val cookieValue = request.getParameter("tgt");
                if (StringUtils.isNotBlank(cookieValue)) {
                    int i = cookieValue.indexOf("TGT-");
                    if (i > -1) {
                        val theCookieValue = this.casCookieValueManager.buildCookieValue(cookieValue, request);
                        cookie = createCookie(theCookieValue);
                    } else {
                        cookie = createCookie(cookieValue);
                    }
                }
            } // y9 add end

            return Optional.ofNullable(cookie).map(ck -> this.casCookieValueManager.obtainCookieValue(ck, request)).orElse(null);
        } catch (final Exception e) {
            LoggingUtils.warn(LOGGER, e);
        }
        return null;
    }

    @Override
    public void setCookieDomain(final String cookieDomain) {
        super.setCookieDomain(StringUtils.defaultIfEmpty(cookieDomain, null));
    }
}
