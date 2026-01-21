package dwe.holding.vmas.controller.form;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;


@Configuration
@AllArgsConstructor
public class VmasVisitFormConfiguration {
    private final ObjectMapper objectMapper;

//    @Bean
//    @SessionScope
//    public VisitForm visitForm() {
//
//        VmasUserPreferences prefData = objectMapper.readValue(AutorisationUtils.getCurrentUserJsonPref(), VmasUserPreferences.class);
//        return new VisitForm(
//                prefData.getVisitVisitInfo().booleanValue(),prefData.getVisitAnalyseInfo()
//
//
//        );
//    }
}