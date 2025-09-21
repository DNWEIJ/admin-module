package dwe.holding.generic.admin.security.local;


import dwe.holding.generic.admin.security.AdminAuthenticationFilter;
import dwe.holding.generic.admin.security.TenantAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import static org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED;
import static org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.withDefaults;

@EnableWebSecurity
@Configuration
@Profile("local")
public class SecurityConfigLocal {

    @Autowired
    private TenantAuthenticationProvider customAuthenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager, new AdminAuthenticationFilterLocal());
        PathPatternRequestMatcher.Builder mvc = withDefaults();

        authFilter.setRequestMatcher(mvc.matcher(HttpMethod.POST, "/login"));
        authFilter.setSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/index"));
        authFilter.setFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error=true"));

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(new CookieCsrfTokenRepository())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .authorizeHttpRequests(
                        (requests) -> requests
                                .requestMatchers("/login", "/error",
                                        "/templates/**",
                                        "/images/**",
                                        "/action-table/**", "/fontawesome/**", "/pico/**", "/tabulator/**", "/ownstyle/**"
                                ).permitAll()
                                .anyRequest()
                                .authenticated()
                )

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
                .addFilterAt(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(customAuthenticationProvider);
    }
}