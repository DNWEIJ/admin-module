package dwe.holding.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
        "dwe.holding.admin.expose",
        "dwe.holding.customer"
})
@EnableJpaRepositories(basePackages = {
        "dwe.holding.admin.authorisation",
        "dwe.holding.admin.preferences",
        "dwe.holding.customer.client.repository",
        "dwe.holding.customer.lookup.repository"
})
@EntityScan(basePackages = {
        "dwe.holding.customer.client.model",
        "dwe.holding.customer.model.lookup",
        "dwe.holding.shared.model",
        "dwe.holding.admin.model"
})
@Slf4j
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}