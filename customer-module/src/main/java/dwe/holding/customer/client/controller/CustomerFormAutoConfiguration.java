package dwe.holding.customer.client.controller;

import dwe.holding.customer.client.controller.form.CustomerForm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerFormAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CustomerForm.class)
    /**
     * inital definition.
     * In VMAS we have defined properties to be able to set preferences.
     * If required, this bean can be overwritten in VMAS to set the default values from userpreferences
     */
    CustomerForm createCustomerForm() {
        return new CustomerForm(true, false, false, false);
    }
}