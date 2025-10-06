package dwe.holding.generic.admin.security;

import dwe.holding.generic.admin.autorisation.function_role.FunctionQueryCriteria;
import dwe.holding.generic.admin.autorisation.user.UserRepository;
import dwe.holding.generic.admin.model.Function;
import dwe.holding.generic.admin.model.IPSecurity;
import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.model.UserPreferences;
import dwe.holding.generic.admin.model.type.YesNoEnum;
import dwe.holding.generic.admin.preferences.UserPreferencesRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class TenantAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;
    private final FunctionQueryCriteria functionQueryCriteria;
    private final UserPreferencesRepository userPreferencesRepository;

    public TenantAuthenticationProvider(UserRepository userRepository, FunctionQueryCriteria functionQueryCriteria, UserPreferencesRepository userPreferencesRepository) {
        this.userRepository = userRepository;
        this.functionQueryCriteria = functionQueryCriteria;
        this.userPreferencesRepository = userPreferencesRepository;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {

        // No password entered?
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        if (!passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    @Override
    protected UserDetails retrieveUser(String concatUsernameShortCode, UsernamePasswordAuthenticationToken authentication) {

        String[] usernameAndShortCode = StringUtils.split(concatUsernameShortCode, String.valueOf(Character.LINE_SEPARATOR));
        if (usernameAndShortCode == null || usernameAndShortCode.length != 2) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badcodeCredentials", "Bad credentials"));
        }

        List<User> users = userRepository.findByAccountWithMemberAndLocals(usernameAndShortCode[0]);
        if (users.isEmpty()) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badcodeCredentials", "Bad credentials"));
        }

        // reduce the list to matching on member.shortCode
        List<User> usersList = users.stream().filter(user -> user.getMember().getShortCode().equals(usernameAndShortCode[1])).toList();
        if (usersList.size() != 1) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badcodeCredentials", "Bad credentials"));
        }
        User user = usersList.getFirst();

        if (user.getMember().getPassword().equals(user.getPassword())) {
            user.setChangePassword(true);
        }

        if (YesNoEnum.No.equals(user.getMember().getActive())) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.memeberaccount_disconnected",
                    "Unfortunately, this account has been disconnected. Please contact your internal administrator."));
        }

        // determine if the user is enabled or not
        if (YesNoEnum.No.equals(user.getLoginEnabled())) {
            throw new BadCredentialsException(
                    messages.getMessage("TenantAuthenticationProvider.login_enabled", "Unfortunalty, you have been disabled. Please contact your internal administrator."));
        }

        // TODO rewrite to stream maybe?        //  check the ipnumbers, if they are set.
        if (!(user.getIpNumbers().isEmpty())) {
            IPSecurity[] array = user.getIpNumbers().toArray(new IPSecurity[user.getIpNumbers().size()]);
            WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
            String ipnumber = details.getRemoteAddress();
            boolean check_ip = true;
            for (int i = 0; i < array.length; i++) {
                if (array[i].getIpnumber().equals(ipnumber)) {
                    check_ip = false;
                }
            }
            if (check_ip) {
                throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.ipnumber",
                        "You are using the application from an unauthorized IPNumber location. Please contact your internal administrator."));
            }

        }

        Collection<? extends GrantedAuthority> authorities = getGrantedAuthoritiesFromRolAndFuncties(user);

        // create details
        AdminUserDetails adminUserDetails = new AdminUserDetails(usernameAndShortCode[0], user.getPassword(), true, true, true, true, authorities);
        adminUserDetails.setUser(user);
        Optional<UserPreferences> optional = userPreferencesRepository.findByUserIdAndMemberIdAndLocalMemberId(user.getId(), user.getMember().getId(), user.getMemberLocalId());

        adminUserDetails.setUserPref(optional.orElse(new UserPreferences()));
        // update status
        LocalDate now = LocalDate.now();
        if (user.getLastVisitDate() == null || !now.equals(user.getLastVisitDate())) {
            user.setLastVisitDate(now);
            if (user.getNumberOfVisits() == null) {
                user.setNumberOfVisits(1L);
            } else {
                user.setNumberOfVisits(user.getNumberOfVisits() + 1);
            }
            userRepository.save(user);
        }
        return adminUserDetails;
    }

    Collection getGrantedAuthoritiesFromRolAndFuncties(User user) {

        if (user.getUserRoles() != null) {
            List<Function> functionList = functionQueryCriteria.process(user.getId());
            GrantedAuthority[] grantedAuthorities = new GrantedAuthority[functionList.size()];

            int grantedAuthorityIndex = 0;

            for (Function function : functionList) {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(function.getName().toUpperCase());
                grantedAuthorities[grantedAuthorityIndex] = grantedAuthority;
                grantedAuthorityIndex++;
            }
            return Arrays.asList(grantedAuthorities);
        }
        return List.of();
    }
}