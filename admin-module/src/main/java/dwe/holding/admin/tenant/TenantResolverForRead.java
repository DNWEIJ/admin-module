package dwe.holding.admin.tenant;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantResolverForRead implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        if (AutorisationUtils.isLoggedIn()) {
            return AutorisationUtils.getCurrentUserMid().toString();
        } else {
            return "77";
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}