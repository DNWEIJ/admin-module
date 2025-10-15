package dwe.holding.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
//        "dwe.holding.generic.admin.authorisation", "dwe.holding.generic.admin.exception", "dwe.holding.generic.admin.expose",
//        "dwe.holding.generic.admin.security", "dwe.holding.generic.admin.setup",
        "dwe.holding.customer"
})
@EnableJpaRepositories(basePackages = {
 //       "dwe.holding.generic.admin.authorisation", "dwe.holding.generic.admin.preferences"
        "dwe.holding.customer.repository",
//        "dwe.holding.customer.migration.repository"
})
@EntityScan(basePackages = {
   //     "dwe.holding.generic.admin.model",
        "dwe.holding.customer.model"
})
@Slf4j
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}