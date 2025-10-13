package dwe.holding.cartracker;

import dwe.holding.generic.admin.security.AutorisationUtils;
import dwe.holding.generic.admin.setup.SetupAdminService;
import dwe.holding.cartracker.setup.MigrationService;
import dwe.holding.cartracker.setup.SetupCarTrackerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
        "dwe.holding.generic.admin.authorisation", "dwe.holding.generic.admin.exception", "dwe.holding.generic.admin.expose",
        "dwe.holding.generic.admin.security", "dwe.holding.generic.admin.setup",
        "dwe.holding.cartracker"
})
@EnableJpaRepositories(basePackages = {
        "dwe.holding.generic.admin.authorisation", "dwe.holding.generic.admin.preferences",
        "dwe.holding.cartracker.repository",
        "dwe.holding.cartracker.migration.repository"
})
@EntityScan(basePackages = {
        "dwe.holding.generic.admin.model",
        "dwe.holding.cartracker.model",
        "dwe.holding.cartracker.migration.model"
})
@Slf4j
public class CarTrackerApplication implements CommandLineRunner {

    @Autowired
    SetupCarTrackerService setupCarTrackerService;
    @Autowired
    SetupAdminService setupAdminService;
    @Autowired
    MigrationService migrationService;

    public static void main(String[] args) {
        SpringApplication.run(CarTrackerApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        // This fetches the current logged-in user (Spring Security)
        return () -> {
            if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
                log.error("No username for auditor aware");
                return Optional.ofNullable("startupData");
            } else {
                return Optional.ofNullable(AutorisationUtils.getCurrentUserAccount());
            }
        };
    }

    @Override
    public void run(String... args) {
        setupAdminService.init();
        setupCarTrackerService.init();
        migrationService.init();
    }
}