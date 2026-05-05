package dwe.holding.admin.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
/**
 * Many queries from htmx are firing multiple times even on the same page, for the same data set.
 * Since we do not want to receive all these duplicate calls, we return them with a cache header set to 10 secs
 */
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
