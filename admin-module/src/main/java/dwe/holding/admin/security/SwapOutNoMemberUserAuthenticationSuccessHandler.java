package dwe.holding.admin.security;

import dwe.holding.admin.model.notenant.Member;
import dwe.holding.admin.model.tenant.User;
import dwe.holding.admin.transactional.TransactionalUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
public class SwapOutNoMemberUserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final String defaultTargetUrl;
    private final TransactionalUserService transactionalUserService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        AdminUserDetails userDetails = (AdminUserDetails) authentication.getPrincipal();
        // first swap in a pseudo User so we will retrieve the memeberId when loading the full user
        List<String> roles = List.of("dummy");
        userDetails.setUser(User.builder().member(
                                Member.builder().id(userDetails.getUserNoMember().getMemberId()).build()
                        )
                        .id(userDetails.getUserNoMember().getId())
                        .roles(roles)
                        .build()
        );
        userDetails.setUser(transactionalUserService.getByIdLazy_LoadingAllData(userDetails.getUserNoMember().getId()));
        userDetails.setUserNoMember(null);
        response.sendRedirect(defaultTargetUrl);
    }
}