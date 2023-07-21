package org.apereo.cas.services.web;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Nonnull;

import org.springframework.context.support.ResourceBundleMessageSource;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * This is {@link CasThemeResourceBundleMessageSource}.
 *
 * @author Misagh Moayyed
 * @since 6.6.0
 */
@Slf4j
public class CasThemeResourceBundleMessageSource extends ResourceBundleMessageSource {
    @Override
    protected ResourceBundle doGetBundle(@Nonnull final String basename, @Nonnull final Locale locale) {
        try {
            // 将basename改为messages，不然会查找不到主题
            val bundle = ResourceBundle.getBundle("messages", locale, getBundleClassLoader());
            if (bundle != null && !bundle.keySet().isEmpty()) {
                return bundle;
            }
        } catch (final Exception e) {
            LOGGER.debug(e.getMessage(), e);
        }
        return null;
    }
}
