package dwe.holding.admin.tenant;

import dwe.holding.admin.security.AutorisationUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantResolverForRead implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        if (AutorisationUtils.isLoggedIn()) {
            return AutorisationUtils.getCurrentUserMid().toString();
        } else {
            return "0";
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}