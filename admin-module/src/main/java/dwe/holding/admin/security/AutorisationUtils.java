package dwe.holding.admin.security;


import dwe.holding.admin.model.LocalMember;
import dwe.holding.admin.model.LocalMemberTax;
import dwe.holding.admin.model.Member;
import dwe.holding.admin.model.User;
import dwe.holding.admin.model.type.LanguagePrefEnum;
import dwe.holding.admin.model.type.PersonnelStatusEnum;
import dwe.holding.shared.model.frontend.PresentationElement;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Wrapper to retrieve the String Security context user. Wrapper will handle validation and throws an exception if needs be.
 */
public class AutorisationUtils {

    protected AutorisationUtils() {
    }

    // only replace the user, updates every other field automatically
    public static void setCurrentUser(User user) {
        getCurrentUserDetails().setUser(user);
    }

    public static String getCurrentLocalMemberName() {
        return getCurrentMember().getLocalMembers().stream()
                .filter(a -> a.getId().equals(getCurrentUserMlid())).findFirst()
                .orElseThrow().getLocalMemberName();
    }

    public static Member getCurrentMember() {
        return getCurrentUserDetails().getUser().getMember();
    }

    public static Long getCurrentUserMid() {
        return getCurrentUserDetails().getUser().getMember().getId();
    }

    public static String getCurrentMemberPassword() {
        return getCurrentUserDetails().getUser().getMember().getPassword();
    }

    public static Long getCurrentUserId() {
        return getCurrentUserDetails().getUser().getId();
    }

    public static String getCurrentUserAccount() {
        return getCurrentUserDetails().getUser().getAccount();
    }

    public static Long getCurrentUserMlid() {
        return getCurrentUserDetails().getUser().getMemberLocalId();
    }

    public static String getCurrentUserJsonPref() {
        return getCurrentUserDetails().getUser().getMetaUserPreferences().getPreferencesJson();
    }

    public static boolean isNewUser() {
        return getCurrentUserDetails().getUser().isChangePassword();
    }

    public static boolean isLocalMemberRequired() {
        return getCurrentUserDetails().getUser().getMember().getLocalMemberSelectRequired().equals(YesNoEnum.Yes);
    }

    public static boolean hasRole(String role) {
        return getCurrentUserDetails().getUser().getRoles().contains(role);
    }

    public static boolean hasStatus(PersonnelStatusEnum status) {
        return status.equals(getCurrentUserDetails().getUser().getPersonnelStatus());
    }

    public static boolean isLoggedIn() {
        try {
            return !getCurrentUserDetails().getUser().getRoles().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public static List<PresentationElement> getLocalMemberList() {
        return AutorisationUtils.getCurrentMember().getLocalMembers()
                .stream().map(f -> new PresentationElement(f.getId(), f.getLocalMemberName(), true))
                .sorted(Comparator.comparing(PresentationElement::getName)).toList();
    }

    public static UserSettings getCurrentUserSettings() {
        return new UserSettings(getCurrentUserMlid(), getCurrentUserDetails().getUser().getLanguage());
    }

    public static LocalMember getCurrentLocalMember() {
        return getCurrentUserDetails().getUser().getMember().getLocalMembers().stream()
                .filter(a -> a.getId().equals(getCurrentUserMlid())).findFirst().orElseThrow();
    }

    public static LocalMemberTax getVatPercentages(@NotNull LocalDate date) {
        for (LocalMemberTax localTax : getCurrentLocalMember().getMemberLocalTaxs()) {
            LocalDate startDate = localTax.getStartDate();
            LocalDate endDate = localTax.getEndDate();
            if ((date.isAfter(startDate) || date.equals(startDate)) && date.isBefore(endDate)) {
                return localTax;
            }
        }
        // should never happen, end date is set to 2999-12-31
        throw new RuntimeException("No tax found for date " + date);
    }

    public static void setTempGenericStorage(String str) {
        getCurrentUserDetails().setTempGenericStorage(str);
    }

    public static String getTempGenericStorage() {
        return getCurrentUserDetails().getTempGenericStorage();
    }

    public record UserSettings(Long localMemberId, LanguagePrefEnum language) {
    }

    private static AdminUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof dwe.holding.admin.security.AdminUserDetails) {
                return (AdminUserDetails) principal;
            } else {
                throw new RuntimeException("No principal found in security context");
            }
        } else {
            throw new RuntimeException("No authentication found in security context");
        }
    }
}