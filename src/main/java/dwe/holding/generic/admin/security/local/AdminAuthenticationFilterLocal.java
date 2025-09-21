package dwe.holding.generic.admin.security.local;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

public class AdminAuthenticationFilterLocal implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        return getAuthRequest(request);
    }

    private UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest request) {
        if (request.getMethod().equals("POST")) {
            String username = "daan";
            String password = "pas";
            String domain = "DWE";

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