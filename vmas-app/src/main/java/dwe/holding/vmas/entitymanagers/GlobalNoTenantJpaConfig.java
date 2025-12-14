package dwe.holding.vmas.entitymanagers;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        // <-- all repos servicing model without having the memberId defined
        basePackages = "dwe.holding.admin.authorisation.notenant",

        entityManagerFactoryRef = "globalEntityManagerFactory",
        transactionManagerRef = "globalTransactionManager"
)
public class GlobalNoTenantJpaConfig {

    @Bean(name = "globalEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean globalEntityManagerFactory(
            @Qualifier("globalDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder,
            ObjectProvider<JpaProperties> jpaPropertiesProvider
    ) {
        JpaProperties jpaProperties = jpaPropertiesProvider.getIfAvailable(JpaProperties::new);

        Map<String, Object> props = new HashMap<>(jpaProperties.getProperties());
        // Ensure multi-tenancy is OFF for global unit
        props.remove("hibernate.multiTenancy");
        props.remove("hibernate.tenant_identifier_resolver");

        return builder
                .dataSource(dataSource)
                // <-- global ENTITIES ONLY (Member, etc.)
                .packages(
                        "dwe.holding.admin.model.notenant",
                        "dwe.holding.customer.client.model",
                        "dwe.holding.customer.model.lookup",
                        "dwe.holding.shared.model",
                        "dwe.holding.salesconsult.sales.model",
                        "dwe.holding.salesconsult.consult.model",
                        "dwe.holding.supplyinventory.model"
                )
                .persistenceUnit("global")
                .properties(props)
                .build();
    }

    @Bean(name = "globalTransactionManager")
    public JpaTransactionManager globalTransactionManager(
            @Qualifier("globalEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}