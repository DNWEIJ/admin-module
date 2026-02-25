package dwe.holding.vmas.local;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

public class UserLocaleResolver implements LocaleResolver {
    private final Locale defaultLocale = Locale.ENGLISH;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return defaultLocale;
        }
        try {
            return Locale.forLanguageTag(AutorisationUtils.getCurrentUserSettings().language().getDatabaseField());
        } catch (Exception e) {
            return defaultLocale;
        }
    }

    @Override
    public void setLocale(HttpServletRequest request,HttpServletResponse response,Locale locale) {

    }
}
