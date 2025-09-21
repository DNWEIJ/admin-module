package dwe.holding.generic.admin.security;


import dwe.holding.generic.admin.model.LocalMember;
import dwe.holding.generic.admin.model.Member;
import dwe.holding.generic.admin.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Wrapper to retrieve the String Security context user. Wrapper will handle validation and throws an exception if needs be.
 */
public final class AutorisationUtils {
    private static final String ERROR_CODE_NOT_LOGGED_IN = "SYS-10008";

    private AutorisationUtils() {
        // Utility class. Do not create an instance.
    }

    public static Member getCurrentMember() {
        return getCurrentUser().getUser().getMember();
    }

    public static Long getCurrentUserMid() {
        return getCurrentUser().getUser().getMember().getId();
    }

    public static String getCurrentMemberPassword() {
        return getCurrentUser().getUser().getMember().getPassword();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getUser().getId();
    }

    public static String getCurrentUserAccount() {
        return getCurrentUser().getUser().getAccount();
    }

    public static List<LocalMember> getCurrentUserLocals() {
        return new ArrayList<LocalMember>(getCurrentMember().getLocalMembers());
    }

    public static LocalMember getCurrentUserMlid(Long mlid) {

        Object[] memlocals = getCurrentUser().getUser().getMember().getLocalMembers().toArray();
        for (int i = 0; i <= memlocals.length; i++) {
            LocalMember memlocal = (LocalMember) memlocals[i];
            if (memlocal.getId().equals(mlid)) {
                return memlocal;
            }
        }
        return null;
    }

    public static boolean isNewUser() {
        return getCurrentUser().getUser().isChangePassword();
    }

    public static Collection<GrantedAuthority> getCurrentAuthorities() {
        return getCurrentUser().getAuthorities();
    }

    private static AdminUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof AdminUserDetails) {
                return (AdminUserDetails) principal;
            } else {
                throw new RuntimeException(ERROR_CODE_NOT_LOGGED_IN);
            }
        } else {
            throw new RuntimeException(ERROR_CODE_NOT_LOGGED_IN);
        }
    }

    public static void setCurrentUser(User user) {
        getCurrentUser().setUser(user);
    }

}