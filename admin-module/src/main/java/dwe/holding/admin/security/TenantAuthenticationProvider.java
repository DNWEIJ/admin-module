package dwe.holding.admin.security;

import dwe.holding.admin.authorisation.tenant.role.FunctionRepository;
import dwe.holding.admin.model.notenant.Function;
import dwe.holding.admin.model.tenant.IPSecurity;
import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.transactional.TransactionalUserService;
import dwe.holding.shared.model.type.YesNoEnum;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
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

@Component
public class TenantAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final TransactionalUserService transactionalUserService;
    private final FunctionRepository functionRepository;

    public TenantAuthenticationProvider(TransactionalUserService transactionalUserService, FunctionRepository functionRepository) {
        this.transactionalUserService = transactionalUserService;
        this.functionRepository = functionRepository;
    }

    enum PasswordState {
        success, succesAndUpdate,
        failure
    }

    TenantAuthenticationProvider.PasswordState validatePasswordChecks(String inputPassword, String dbPassword) {

        // No password entered?
        if (inputPassword == null || inputPassword.isEmpty()) {
            return PasswordState.failure;
        }

        if (!passwordEncoder.matches(inputPassword, dbPassword)) {
            // maybe we have an old md5, if so, change to new bcrypt
            if (LegacyMd5Encoder.matches(inputPassword, dbPassword)) {
                return PasswordState.succesAndUpdate;
            } else {
                return PasswordState.failure;
            }
        }
        return PasswordState.success;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // update status
        AdminUserDetails adminUser  = (AdminUserDetails) userDetails;
        User user = adminUser.getUser();

        LocalDate now = LocalDate.now();
        if (user.getLastVisitDate() == null || !now.equals(user.getLastVisitDate())) {
            user.setLastVisitDate(now);
            if (user.getNumberOfVisits() == null) {
                user.setNumberOfVisits(1L);
            } else {
                user.setNumberOfVisits(user.getNumberOfVisits() + 1);
            }
            transactionalUserService.save(user);
        }
    }

    @Override
    protected UserDetails retrieveUser(String concatUsernameShortCode, UsernamePasswordAuthenticationToken authentication) {

        String[] usernameAndShortCode = StringUtils.split(concatUsernameShortCode, String.valueOf(Character.LINE_SEPARATOR));
        if (usernameAndShortCode == null || usernameAndShortCode.length != 2) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badcodeCredentials", "Bad credentials"));
        }

        List<User> users = transactionalUserService.getByAccount(usernameAndShortCode[0]);

        if (users.isEmpty()) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badcodeCredentials", "Bad credentials"));
        }

        // reduce the list to matching on member.shortCode
        List<User> usersList = users.stream().filter(user -> user.getMember().getShortCode().equals(usernameAndShortCode[1])).toList();
        if (usersList.size() != 1) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badcodeCredentials", "Bad credentials"));
        }

        User user = transactionalUserService.getByIdLazy_LoadingAllData(usersList.getFirst().getId());

        PasswordState state = validatePasswordChecks(authentication.getCredentials().toString(), user.getPassword());
        if (state.equals(PasswordState.failure)) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badcodeCredentials", "Bad credentials"));
        }
        if (state.equals(PasswordState.succesAndUpdate)) {
            user.setPassword(passwordEncoder.encode(authentication.getCredentials().toString()));
            user = transactionalUserService.save(user);
        }
        // todo password on member is empty?
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
                    messages.getMessage("TenantAuthenticationProvider.login_enabled", "Unfortunately, you have been disabled. Please contact your internal administrator."));
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
                throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.ipNumber",
                        "You are using the application from an unauthorized IpNumber location. Please contact your internal administrator."));
            }

        }

        // create details
        AdminUserDetails adminUserDetails = new AdminUserDetails(usernameAndShortCode[0], user.getPassword(),
                true, true, true, true, getGrantedAuthoritiesFromRolAndFunction(user));
        adminUserDetails.setUser(user);
        return adminUserDetails;
    }

    Collection<? extends GrantedAuthority> getGrantedAuthoritiesFromRolAndFunction(User user) {

        if (user.getUserRoles() != null) {
            List<Function> functionList =  functionRepository.getAllFunctionsForUser(user.getId());
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