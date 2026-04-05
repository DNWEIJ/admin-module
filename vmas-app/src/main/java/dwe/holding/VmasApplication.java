package dwe.holding;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import dwe.holding.vmas.local.UserLocaleResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Optional;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
        "dwe.holding.admin.*",
        "dwe.holding.customer.*",
        "dwe.holding.salesconsult.*",
        "dwe.holding.supplyinventory.*",
        "dwe.holding.reporting.*",
        "dwe.holding.vmas.*",
        "dwe.holding.admin.tenant"
})

@Slf4j
@EnableAsync
public class VmasApplication implements CommandLineRunner {

//    @Autowired
//    SetupAdminService setupAdminService;

//    @Autowired
//    UpdateDatabase updateDatabase;

    static void main(String[] args) {
        SpringApplication.run(VmasApplication.class, args);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new UserLocaleResolver();
    }


    @Bean
    public AuditorAware<String> auditorProvider() {
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
//        setupAdminService.init();
//        updateDatabase.processAllCustomerBalance();
//        updateDatabase.processAllVisitsBalance();
    }
}