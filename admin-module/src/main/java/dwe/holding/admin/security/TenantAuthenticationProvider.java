package dwe.holding.admin.security;

import dwe.holding.admin.authorisation.tenant.user.UserNoMemberRepository;
import dwe.holding.admin.authorisation.tenant.user.UserRoleRepository;
import dwe.holding.admin.model.tenant.IPSecurity;
import dwe.holding.admin.model.tenant.UserNoMember;
import dwe.holding.admin.model.tenant.UserRole;
import dwe.holding.admin.transactional.TransactionalUserService;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@AllArgsConstructor
@Slf4j
public class TenantAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final TransactionalUserService transactionalUserService;
    private final UserNoMemberRepository UserNoMemberRepository;
    private final UserRoleRepository userRoleRepository;


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
        AdminUserDetails usrDetails = (AdminUserDetails) userDetails;
        UserNoMember userNoMember = ((AdminUserDetails) userDetails).getUserNoMember();

        //    User user = transactionalUserService.getByIdLazy_LoadingAllData(userNoMember.getId());

        if (userNoMember.isNewEncryptionPassword()) {
            // change password to the new encryption format
            userNoMember.setPassword(passwordEncoder.encode(authentication.getCredentials().toString()));
        }

        LocalDate now = LocalDate.now();
        if (userNoMember.getLastVisitDate() == null || !now.equals(userNoMember.getLastVisitDate())) {
            userNoMember.setLastVisitDate(now);
            if (userNoMember.getNumberOfVisits() == null) {
                userNoMember.setNumberOfVisits(1L);
            } else {
                userNoMember.setNumberOfVisits(userNoMember.getNumberOfVisits() + 1);
            }
        }
        userNoMember = UserNoMemberRepository.save(userNoMember);
        //usrDetails.setUser(user);
        usrDetails.setUserNoMember(userNoMember);
    }

    @Override
    protected UserDetails retrieveUser(String concatUsernameShortCode, UsernamePasswordAuthenticationToken authentication) {

        String[] usernameAndShortCode = StringUtils.split(concatUsernameShortCode, String.valueOf(Character.LINE_SEPARATOR));
        if (usernameAndShortCode == null || usernameAndShortCode.length != 2) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badcodeCredentials", "Bad credentials"));
        }

        List<UserNoMember> users = transactionalUserService.getByAccount(usernameAndShortCode[0]);

        if (users.isEmpty()) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badcodeCredentials", "Bad credentials"));
        }

        // reduce the list to matching on member.shortCode
        List<UserNoMember> usersList = users.stream().filter(user -> user.getMember().getShortCode().equals(usernameAndShortCode[1])).toList();
        if (usersList.size() != 1) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badcodeCredentials", "Bad credentials"));
        }

        UserNoMember userNoMember = usersList.getFirst();
        PasswordState state = validatePasswordChecks(authentication.getCredentials().toString(), userNoMember.getPassword());
        if (state.equals(PasswordState.failure))
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.badcodeCredentials", "Bad credentials"));

        if (state.equals(PasswordState.succesAndUpdate)) {
            log.info("changing the encryptionType for: u:p --" + userNoMember.getAccount() + "|" + userNoMember.getPassword() + "--");
            userNoMember.setNewEncryptionPassword(true);
        }

        if (userNoMember.getMember().getPassword() != null && !userNoMember.getMember().getPassword().isEmpty()) {
            if (userNoMember.getMember().getPassword().equals(userNoMember.getPassword())) {
                userNoMember.setChangePassword(true);
            }
        }

        if (YesNoEnum.No.equals(userNoMember.getMember().getActive())) {
            throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.memeberaccount_disconnected",
                    "Unfortunately, this account has been disconnected. Please contact your internal administrator."));
        }

        // determine if the user is enabled or not
        if (YesNoEnum.No.equals(userNoMember.getLoginEnabled())) {
            throw new BadCredentialsException(
                    messages.getMessage("TenantAuthenticationProvider.login_enabled", "Unfortunately, you have been disabled. Please contact your internal administrator."));
        }

        List<IPSecurity> ipnumbers = transactionalUserService.getIpNumbersForUserId(userNoMember.getId());
        if (!ipnumbers.isEmpty()) {
            String myIpNumber = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();

            if (ipnumbers.stream().filter(ip -> ip.getIpnumber().equals(myIpNumber)).findFirst().isEmpty()) {
                throw new BadCredentialsException(messages.getMessage("TenantAuthenticationProvider.ipNumber",
                        "You are using the application from an unauthorized IpNumber location. Please contact your internal administrator."));
            }
        }

        // create details
        AdminUserDetails adminUserDetails = new AdminUserDetails(usernameAndShortCode[0], userNoMember.getPassword(),
                true, true, true, true, getGrantedAuthoritiesFromRolAndFunction(userNoMember));
        adminUserDetails.setUserNoMember(userNoMember);
        return adminUserDetails;
    }

    Collection<? extends GrantedAuthority> getGrantedAuthoritiesFromRolAndFunction(UserNoMember user) {

        List<UserRole> userRoles = userRoleRepository.findByUser_id(user.getId());
        GrantedAuthority[] grantedAuthorities = new GrantedAuthority[userRoles.size()];

        int grantedAuthorityIndex = 0;

        for (UserRole userRole : userRoles) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userRole.getRole().getId().toString());
            grantedAuthorities[grantedAuthorityIndex] = grantedAuthority;
            grantedAuthorityIndex++;
        }
        return Arrays.asList(grantedAuthorities);

    }
}