package org.apereo.cas.services;

import java.util.Collection;
import java.util.function.BiPredicate;

import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.springframework.core.Ordered;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;

/**
 * This is {@link DefaultServicesManagerRegisteredServiceLocator}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DefaultServicesManagerRegisteredServiceLocator implements ServicesManagerRegisteredServiceLocator {
    private int order = Ordered.LOWEST_PRECEDENCE;

    private BiPredicate<RegisteredService, Service> registeredServiceFilter = (registeredService, service) -> {
        val supportedType = supports(registeredService, service);
        return supportedType && registeredService.matches(service.getId());
    };

    @Override
    public RegisteredService locate(final Collection<RegisteredService> candidates, final Service service) {
        return candidates.stream().filter(registeredService -> supports(registeredService, service))
            .filter(registeredService -> registeredServiceFilter.test(registeredService, service)).findFirst()
            .orElse(null);
    }

    @Override
    public boolean supports(final RegisteredService registeredService, final Service service) {
        return (OAuthRegisteredService.class.isAssignableFrom(registeredService.getClass())
            && registeredService.getFriendlyName().equalsIgnoreCase("OAuth2 Client"))
            || (CasRegisteredService.class.isAssignableFrom(registeredService.getClass())
                && registeredService.getFriendlyName().equalsIgnoreCase(CasRegisteredService.FRIENDLY_NAME))
            || (RegexRegisteredService.class.isAssignableFrom(registeredService.getClass())
                && registeredService.getFriendlyName().equalsIgnoreCase(CasRegisteredService.FRIENDLY_NAME));
    }
}
