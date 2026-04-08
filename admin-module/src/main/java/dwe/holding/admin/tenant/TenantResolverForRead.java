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
            // TODO put to zeor, for now we only have GWZ and we need it for the migration of balance
           return 77L;
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}