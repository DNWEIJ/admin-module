package dwe.holding.cartracker;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.admin.setup.SetupAdminService;
import dwe.holding.cartracker.setup.MigrationService;
import dwe.holding.cartracker.setup.SetupCarTrackerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Optional;


@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
        "dwe.holding.admin.authorisation", "dwe.holding.admin.exception", "dwe.holding.admin.expose",
        "dwe.holding.admin.transactional", "dwe.holding.admin.security", "dwe.holding.admin.setup",
        "dwe.holding.cartracker"
})
@EnableJpaRepositories(basePackages = {
        "dwe.holding.admin.authorisation", "dwe.holding.admin.preferences",
        "dwe.holding.cartracker.repository",
        "dwe.holding.cartracker.migration.repository"
})
@EntityScan(basePackages = {
        "dwe.holding.admin.model",
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
    public ITemplateResolver moduleTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(10);
        resolver.setCheckExistence(true);
        return resolver;
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