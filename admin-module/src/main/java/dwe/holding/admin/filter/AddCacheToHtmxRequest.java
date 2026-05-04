package dwe.holding.admin.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AddCacheToHtmxRequest extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean isHtmx = "true".equals(request.getHeader("HX-Request"));
        boolean isGet = "GET".equalsIgnoreCase(request.getMethod());
        if (isHtmx && isGet) {
            response.setHeader("Cache-Control", "private, max-age=10");
            response.setHeader("Vary", "Cookie");
        }
        filterChain.doFilter(request, response);
    }
}
