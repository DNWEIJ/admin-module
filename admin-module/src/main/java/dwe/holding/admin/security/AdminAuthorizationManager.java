package dwe.holding.admin.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;



@Component
public class AdminAuthorizationManager implements AuthorizationManager {

    private final TenantAccessDecisionVoter tenantAccessDecisionVoter;

    public AdminAuthorizationManager(TenantAccessDecisionVoter tenantAccessDecisionVoter) {
        this.tenantAccessDecisionVoter = tenantAccessDecisionVoter;
    }

    @Override
    public @Nullable AuthorizationResult authorize(Supplier authentication, Object object) {

        int decision = this.tenantAccessDecisionVoter.vote((Authentication) authentication.get(), object);
        switch (decision) {
            case TenantAccessDecisionVoter.ACCESS_GRANTED:
                return new AuthorizationDecision(true);
            case TenantAccessDecisionVoter.ACCESS_DENIED:
                return new AuthorizationDecision(false);
        }
        return null;
    }

    @Override
    public void verify(Supplier authentication, Object object) {
        AuthorizationManager.super.verify(authentication, object);
    }
}