package dwe.holding.generic.admin.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class RedirectToLoginEntryPoint implements AuthenticationEntryPoint {

    private final String loginPath;

    public RedirectToLoginEntryPoint(String loginPath) {
        this.loginPath = loginPath;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException)
            throws IOException {
        // Always redirect to login instead of sending 403/401
        response.sendRedirect(loginPath);
    }
}