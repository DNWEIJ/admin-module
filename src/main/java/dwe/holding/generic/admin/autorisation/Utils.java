package dwe.holding.generic.admin.autorisation;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Utils {

    public static boolean isSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.getAuthorities().stream()
                .anyMatch(
                        a -> a.getAuthority().equals("ROLE_SUPER_ADMIN")
                )
        );
    }
}