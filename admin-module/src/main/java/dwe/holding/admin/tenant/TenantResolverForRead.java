package dwe.holding.admin.tenant;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TenantResolverForRead implements CurrentTenantIdentifierResolver {

    @Override
    public Long resolveCurrentTenantIdentifier() {
        if (AutorisationUtils.isLoggedIn()) {
            return AutorisationUtils.getCurrentUserMid();
        } else {
           log.error("No user logged in! THIS SHOULD NOT HAPPEN ON PRODUCTION");
           return 0L;
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}