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
            // TODO set to: 0L, for the first runs we need it for the functionality running during startup.
           return 77L;
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}