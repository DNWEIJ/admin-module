package dwe.holding.admin.tenant;

import dwe.holding.admin.security.AutorisationUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantResolverForRead implements CurrentTenantIdentifierResolver {

    @Override
    public Long resolveCurrentTenantIdentifier() {
        if (AutorisationUtils.isLoggedIn()) {
            return AutorisationUtils.getCurrentUserMid();
        } else {
            return 0L;
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}