package dwe.holding.admin;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.admin.setup.SetupAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


@EnableJpaAuditing
@SpringBootApplication
@Slf4j
public class AdminApplication implements CommandLineRunner {

    @Autowired
    private SetupAdminService setupAdminService;

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        // This fetches the current logged-in user (Spring Security)
        return () -> {
            if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
                log.error("NO username for auditor aware");
                return Optional.ofNullable("system");
            } else {
                return Optional.ofNullable(AutorisationUtils.getCurrentUserAccount());
            }
        };
    }

    @Override
    public void run(String... args) {
          Long memberId = setupAdminService.init();
    }
}