package dwe.holding.generic;

import dwe.holding.generic.admin.security.AutorisationUtils;
import dwe.holding.generic.suppliesandinventory.setup.SetupSuppliesService;
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
        "dwe.holding.generic.admin.security",
        "dwe.holding.generic.suppliesandinventory"
})
@EnableJpaRepositories(basePackages = {
        "dwe.holding.generic.admin.authorisation", "dwe.holding.generic.admin.preferences",
        "dwe.holding.generic.suppliesandinventory.repository"
})
@EntityScan(basePackages = {
        "dwe.holding.generic.admin.model",
        "dwe.holding.generic.suppliesandinventory.model"
})
@Slf4j
public class SupplyInventoryApplication implements CommandLineRunner {

    @Autowired
    private SetupSuppliesService setupSuppliesService;

    public static void main(String[] args) {
        SpringApplication.run(SupplyInventoryApplication.class, args);
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

    @Override
    public void run(String... args) {
        setupSuppliesService.init();
    }
}