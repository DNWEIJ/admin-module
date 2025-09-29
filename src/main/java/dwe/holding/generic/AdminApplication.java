package dwe.holding.generic;

import dwe.holding.generic.admin.security.AutorisationUtils;
import dwe.holding.generic.admin.setup.SetupAdminService;
import dwe.holding.generic.suppliesandinventory.setup.MigrationSuppliesService;
import dwe.holding.generic.teammover.setup.SetupTeamMoverAdminService;
import dwe.holding.generic.teammover.setup.SetupTeamMoverDataService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@SpringBootApplication
@Slf4j
public class AdminApplication implements CommandLineRunner {

    @Autowired
    SetupTeamMoverDataService setupTeamMoverDataService;
    @Autowired
    SetupTeamMoverAdminService setupTeamMoverAdminService;
    @Autowired
    private SetupAdminService setupAdminService;
    @Autowired
    private MigrationSuppliesService migrationSuppliesService;
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        // This fetches the current logged-in user (Spring Security)
        return () -> {
            if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
                log.error("NO username for auditor aware");
                return Optional.ofNullable("startupData");
            } else {
                return Optional.ofNullable(AutorisationUtils.getCurrentUserAccount());
            }
        };
    }

//    @Bean
//    public LayoutDialect layoutDialect() {
//        return new LayoutDialect();
//    }

    @Override
    public void run(String... args) {
        setupAdminService.init();
        // todo fix    migrationSuppliesService.init();
        setupTeamMoverAdminService.init();
        setupTeamMoverDataService.init();
    }
}