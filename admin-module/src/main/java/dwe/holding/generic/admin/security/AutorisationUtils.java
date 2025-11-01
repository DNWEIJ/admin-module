package dwe.holding.generic.admin.security;


import dwe.holding.generic.admin.model.Member;
import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.model.UserPreferences;
import dwe.holding.generic.shared.model.type.YesNoEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * Wrapper to retrieve the String Security context user. Wrapper will handle validation and throws an exception if needs be.
 */
public class AutorisationUtils {

    protected AutorisationUtils() {
    }

    public static String getCurrentLocalMemberName() {
        return getCurrentMember().getLocalMembers().stream()
                .filter(a -> a.getId().equals(getCurrentUserMlid())).findFirst()
                .orElseThrow().getLocalMemberName();
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

    public static Long getCurrentUserMlid() {
        return getCurrentUser().getUser().getMemberLocalId();
    }

    public static Long validateAndReturnLocalMemberId(Long localMemberId) {
        return getCurrentMember().getLocalMembers().stream().
                filter(f -> f.getId().equals(localMemberId))
                .findFirst()
                .orElseThrow()
                .getId();
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
                throw new RuntimeException("No principal found in security context");
            }
        } else {
            throw new RuntimeException("No authentication found in security context");
        }
    }

    public static void setCurrentUser(User user) {
        getCurrentUser().setUser(user);
    }

    public static void setCurrentUserPref(UserPreferences userPreferences) {
        getCurrentUser().setUserPref(userPreferences);
    }

    public static String getCurrentUserJsonPref() {
        return getCurrentUser().getUserPref().getUserPreferencesJson();
    }


    public static boolean isNewUser() {
        return getCurrentUser().getUser().isChangePassword();
    }

    public static boolean isLocalMemberRequired() {
        return getCurrentUser().getUser().getMember().getLocalMemberSelectRequired().equals(YesNoEnum.Yes);
    }

    public static boolean hasRole(String role) {
        return getCurrentUser().getUser().getRoles().contains(role);
    }

    public static boolean isLoggedIn() {
        try {
            return !getCurrentUser().getUser().getRoles().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public static void setInfoObject(InformationObject informationObject) {
     // TODO:    getCurrentUser().setInformationObject(informationObject);
    }
    public static InformationObject getInfoObject() {
        return getCurrentUser().getInformationObject();
    }


}