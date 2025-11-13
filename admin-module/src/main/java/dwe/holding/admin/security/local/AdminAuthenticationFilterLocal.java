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
            // String username = "jeroen";
            // String username = "vera";
             String username = "arjan";
            //String username = "daniel";
            String password = "ZVS!DeEerste!";
            String domain = "ZVS";

//            String username = "daniel";
//            String password = "pas!";
//            String domain = "DWE";


            String usernameDomain = String.format("%s%s%s", username.trim(), String.valueOf(Character.LINE_SEPARATOR), domain);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(usernameDomain, password);
            token.setDetails(detailsSource.buildDetails(request));
            return token;
        }
        return null;
    }
}