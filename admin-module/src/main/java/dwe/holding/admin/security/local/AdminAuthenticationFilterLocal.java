package dwe.holding.admin.security.local;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class AdminAuthenticationFilterLocal implements AuthenticationConverter {
    private final WebAuthenticationDetailsSource detailsSource = new WebAuthenticationDetailsSource();

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
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(usernameDomain, password);
            token.setDetails(detailsSource.buildDetails(request));
            return token;
        }
        return null;
    }
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter("password");
    }
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("username");
    }
    protected String obtainShortCode(HttpServletRequest request) {
        return request.getParameter("shortCode");
    }

}