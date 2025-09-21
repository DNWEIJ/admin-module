package dwe.holding.generic.admin.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static org.springframework.security.access.AccessDecisionVoter.ACCESS_DENIED;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_GRANTED;


@Component
public class AdminAuthorizationManager implements AuthorizationManager {

    private final TenantAccessDecisionVoter tenantAccessDecisionVoter;

    public AdminAuthorizationManager(TenantAccessDecisionVoter tenantAccessDecisionVoter) {
        this.tenantAccessDecisionVoter = tenantAccessDecisionVoter;

    }

    @Override
    public AuthorizationDecision check(Supplier authentication, Object object) {
        int decision = this.tenantAccessDecisionVoter.vote((Authentication) authentication.get(), object, null);
        switch (decision) {
            case ACCESS_GRANTED:
                return new AuthorizationDecision(true);
            case ACCESS_DENIED:
                return new AuthorizationDecision(false);
        }
        return null;
    }
}