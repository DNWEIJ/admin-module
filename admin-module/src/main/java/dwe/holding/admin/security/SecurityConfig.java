package dwe.holding.admin.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED;
import static org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.withDefaults;

@EnableWebSecurity
@Configuration
@Profile("!local")
public class SecurityConfig {

    @Autowired
    private TenantAuthenticationProvider customAuthenticationProvider;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager, AdminAuthorizationManager adminAuthorizationManager) {

        AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager, new AdminAuthenticationFilter());
        PathPatternRequestMatcher.Builder mvc = withDefaults();

        authFilter.setRequestMatcher(mvc.matcher(HttpMethod.POST, "/admin/login"));
        authFilter.setSuccessHandler(new SimpleUrlAuthenticationSuccessHandler(contextPath + "/admin/index"));
        authFilter.setFailureHandler(new SimpleUrlAuthenticationFailureHandler(contextPath + "/admin/login?error=true"));

        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.ALL));

        RequestMatcher publicEndpoints = request -> {
            String req = request.getRequestURI();
            return req.startsWith(contextPath + "/admin/login") ||
                    req.startsWith(contextPath + "/admin/logout") ||
                    req.startsWith(contextPath + "/admin/error") ||
                    req.startsWith(contextPath + "/lib/") ||
                    req.startsWith(contextPath + "/images/");
        };

        // Composite AuthorizationManager
        AuthorizationManager<RequestAuthorizationContext> compositeAuthManager =
                (authenticationSupplier, context) -> {
                    if (publicEndpoints.matches(context.getRequest())) {
                        return new AuthorizationDecision(true); // permitAll
                    }
                    return adminAuthorizationManager.authorize(authenticationSupplier, context);
                };

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(new CookieCsrfTokenRepository().withHttpOnlyFalse())
                )

                .authorizeHttpRequests(authz -> authz
                        .anyRequest().access(compositeAuthManager)
                )
                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(new RedirectToLoginEntryPoint("/admin/login?error=true"))
                                .authenticationEntryPoint((req, resp, authException) -> {
                                    resp.sendRedirect(contextPath + "/admin/login?error=true");
                                })
                                .accessDeniedHandler((req, res, e) -> res.sendRedirect(contextPath + "/admin/login")) // authenticated but forbidden
                )

                // required to persist the security context between requests
                .securityContext(securityContext ->
                        securityContext
                                .requireExplicitSave(false) // ensures context is stored automatically
                )
                // Session management
                .sessionManagement(session ->
                        session
                                .sessionCreationPolicy(IF_REQUIRED)
                                .maximumSessions(1) // limit concurrent sessions
                                .maxSessionsPreventsLogin(false)
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .addLogoutHandler(clearSiteData)
                )
                .addFilterAt(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(customAuthenticationProvider);
    }
}