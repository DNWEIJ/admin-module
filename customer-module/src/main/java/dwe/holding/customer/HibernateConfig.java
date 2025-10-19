package dwe.holding.customer;

import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    static {
        // Force the class to load so Hibernate registers it
        try {
            Class.forName("dwe.holding.generic.shared.model.converter.YesNoEnumConverter");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}