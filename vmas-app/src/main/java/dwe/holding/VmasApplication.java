package dwe.holding;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.admin.setup.SetupAdminService;
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

import java.util.Optional;


@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
        "dwe.holding.admin.*",
        "dwe.holding.customer.*",
        "dwe.holding.salesconsult.*",
        "dwe.holding.supplyinventory.*",
        "dwe.holding.vmas.*"
})
@EnableJpaRepositories(basePackages = {
        "dwe.holding.admin.authorisation",
        "dwe.holding.admin.preferences",
        "dwe.holding.customer.client.repository",
        "dwe.holding.customer.lookup.repository",
        "dwe.holding.salesconsult.sales.repository",
        "dwe.holding.salesconsult.consult.repository",
        "dwe.holding.supplyinventory.repository"
})
@EntityScan(basePackages = {
        "dwe.holding.admin.model",
        "dwe.holding.customer.client.model",
        "dwe.holding.customer.model.lookup",
        "dwe.holding.shared.model",
        "dwe.holding.salesconsult.sales.model",
        "dwe.holding.salesconsult.consult.model",
        "dwe.holding.supplyinventory.model"
})
@Slf4j
public class VmasApplication implements CommandLineRunner {

    @Autowired
    SetupAdminService setupAdminService;

    public static void main(String[] args) {
        SpringApplication.run(VmasApplication.class, args);
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
        setupAdminService.init();
    }
}