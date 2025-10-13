package dwe.holding.generic.admin.security;


import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager, AdminAuthorizationManager adminAuthorizationManager) throws Exception {

        AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager, new AdminAuthenticationFilter());
        PathPatternRequestMatcher.Builder mvc = withDefaults();

        authFilter.setRequestMatcher(mvc.matcher(HttpMethod.POST, "/login"));
        authFilter.setSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/index"));
        authFilter.setFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error=true"));

        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.ALL));

        RequestMatcher publicEndpoints = request -> {
            String req = request.getRequestURI();
            return req.startsWith("/login") ||
                    req.startsWith("/logout") ||
                    req.startsWith("/error") ||
                    req.startsWith("/lib/") ||
                    req.startsWith("/images/");
        };

        // Composite AuthorizationManager
        AuthorizationManager<RequestAuthorizationContext> compositeAuthManager =
                (authenticationSupplier, context) -> {
                    if (publicEndpoints.matches(context.getRequest())) {
                        return new AuthorizationDecision(true); // permitAll
                    }
                    return adminAuthorizationManager.check(authenticationSupplier, context);
                };

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(new CookieCsrfTokenRepository())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))

                .authorizeHttpRequests(authz -> authz
                        .anyRequest().access(compositeAuthManager)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new RedirectToLoginEntryPoint("/login"))
                        .accessDeniedHandler((req, res, e) -> res.sendRedirect("/login")) // authenticated but forbidden
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