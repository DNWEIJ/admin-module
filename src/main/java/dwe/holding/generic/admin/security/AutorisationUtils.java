package dwe.holding.generic.admin.security;


import dwe.holding.generic.admin.model.LocalMember;
import dwe.holding.generic.admin.model.Member;
import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.model.type.YesNoEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Wrapper to retrieve the String Security context user. Wrapper will handle validation and throws an exception if needs be.
 */
public class AutorisationUtils {
    private static final String ERROR_CODE_NOT_LOGGED_IN = "SYS-10008";

    protected AutorisationUtils() {
        // Utility class. Do not create an instance.
    }

    public static String getCurrentLocalMemberName() {
        return getCurrentMember().getLocalMembers().stream().filter(a -> a.getId().equals(getCurrentUserMlid())).findFirst().orElseThrow().getLocalMemberName();
    }

    public static Member getCurrentMember() {
        return getCurrentUser().getUser().getMember();
    }

    public static UUID getCurrentUserMid() {
        return getCurrentUser().getUser().getMember().getId();
    }

    public static String getCurrentMemberPassword() {
        return getCurrentUser().getUser().getMember().getPassword();
    }

    public static UUID getCurrentUserId() {
        return getCurrentUser().getUser().getId();
    }

    public static String getCurrentUserAccount() {
        return getCurrentUser().getUser().getAccount();
    }

    public static UUID getCurrentUserMlid() {
        return getCurrentUser().getUser().getMemberLocalId();
    }

    public static UUID validateAndreturnLocalMemberId(UUID localMemberId) {
        return getCurrentMember().getLocalMembers().stream().
                filter(f -> f.getId().equals(localMemberId))
                .findFirst()
                .orElseThrow()
                .getId();
    }

    public static boolean isNewUser() {
        return getCurrentUser().getUser().isChangePassword();
    }

    public static boolean isLocalMemberRequired() {
        return getCurrentUser().getUser().getMember().getLocalMemberSelectRequired().equals(YesNoEnum.Yes);
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

    public static void setCurrentUserPref(Object jsonPreferences) {
        getCurrentUser().setUserPref(jsonPreferences);
    }

    public static Object getCurrentUserPref() {
        return getCurrentUser().getUserPref();
    }

    public static boolean isRole(String role) {
        return AutorisationUtils.getCurrentAuthorities().contains(new SimpleGrantedAuthority(role));
    }
}