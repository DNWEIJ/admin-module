package dwe.holding.generic.admin.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

public class AdminAuthenticationFilter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        return getAuthRequest(request);
    }

    private UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest request) {
        if (request.getMethod().equals("POST")) {
            String username = obtainUsername(request);
            String password = obtainPassword(request);
            String domain = obtainShortCode(request);

            String usernameDomain = String.format("%s%s%s", username.trim(), String.valueOf(Character.LINE_SEPARATOR), domain);
            return new UsernamePasswordAuthenticationToken(usernameDomain, password);
        }
        return null;
    }

    @Nullable
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter("password");
    }

    @Nullable
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("username");
    }

    @Nullable
    protected String obtainShortCode(HttpServletRequest request) {
        return request.getParameter("shortCode");
    }

}