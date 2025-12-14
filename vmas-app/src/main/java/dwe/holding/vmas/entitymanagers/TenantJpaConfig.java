package dwe.holding.vmas.entitymanagers;


import jakarta.persistence.EntityManagerFactory;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                // all repos that do have memberId defined
                "dwe.holding.admin.authorisation.tenant",
                "dwe.holding.admin.preferences",
                "dwe.holding.customer.client.repository",
                "dwe.holding.customer.lookup.repository",
                "dwe.holding.salesconsult.sales.repository",
                "dwe.holding.salesconsult.consult.repository",
                "dwe.holding.supplyinventory.repository"
        },
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager"
)
public class TenantJpaConfig {

    @Bean(name = "tenantEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
            @Qualifier("tenantDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder,
            ObjectProvider<JpaProperties> jpaPropertiesProvider,
            CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolver
    ) {
        JpaProperties jpaProperties = jpaPropertiesProvider.getIfAvailable(JpaProperties::new);

        Map<String, Object> props = new HashMap<>(jpaProperties.getProperties());

        // Turn on Hibernate discriminator multi-tenancy for THIS EMF only
        props.put("hibernate.multiTenancy", "DISCRIMINATOR");

        // Make sure Hibernate uses your resolver (in case auto-detection doesn’t kick in)
        props.put("hibernate.tenant_identifier_resolver", currentTenantIdentifierResolver);

        return builder
                .dataSource(dataSource)
                // the entity packages
                .packages(
                        "dwe.holding.admin.model",
                        "dwe.holding.customer.client.model",
                        "dwe.holding.customer.model.lookup",
                        "dwe.holding.shared.model",
                        "dwe.holding.salesconsult.sales.model",
                        "dwe.holding.salesconsult.consult.model",
                        "dwe.holding.supplyinventory.model"
                )
                .persistenceUnit("tenant")
                .properties(props)
                .build();
    }

    @Bean(name = "tenantTransactionManager")
    @Primary
    public JpaTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}