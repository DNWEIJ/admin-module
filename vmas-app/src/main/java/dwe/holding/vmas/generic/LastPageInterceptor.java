package dwe.holding.vmas.generic;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
/**
 * Keep track of the previous url, so we can go back to it if neededs be
 */
public class LastPageInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String uri = request.getRequestURI();
            String qs = request.getQueryString();

            String fullUrl = (qs == null) ? uri : uri + "?" + qs;
            request.getSession().setAttribute("LAST_GET_URL", fullUrl);
        }

        return true;
    }
}