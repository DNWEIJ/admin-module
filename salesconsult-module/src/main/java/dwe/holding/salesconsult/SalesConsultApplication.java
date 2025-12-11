package dwe.holding.salesconsult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
        "dwe.holding.admin.*",
        "dwe.holding.customer.client",
        "dwe.holding.customer.lookup",
        "dwe.holding.customer.expose",
        "dwe.holding.salesconsult.*",
        "dwe.holding.supplyinventory.*"
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
public class SalesConsultApplication {
    public static void main(String[] args) {
        SpringApplication.run(SalesConsultApplication.class, args);
    }
}