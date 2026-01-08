package dwe.holding.vmas.controller.form;

import dwe.holding.admin.security.AutorisationUtils;
import dwe.holding.customer.client.controller.form.CustomerForm;
import dwe.holding.vmas.model.VmasUserPreferences;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;
import tools.jackson.databind.ObjectMapper;


@Configuration
@AllArgsConstructor
public class VmasCustomerFormConfiguration {
    private final ObjectMapper objectMapper;

    @Bean
    @SessionScope
    public CustomerForm customerForm() {

        VmasUserPreferences prefData = objectMapper.readValue(AutorisationUtils.getCurrentUserJsonPref(), VmasUserPreferences.class);
        return new CustomerForm(
                prefData.getSearchCustStart().booleanValue(),
                prefData.getSearchCustStreet().booleanValue(),
                prefData.getSearchCustNameTelephone().booleanValue(),
                prefData.getSearchCustPet().booleanValue()
        );
    }
}