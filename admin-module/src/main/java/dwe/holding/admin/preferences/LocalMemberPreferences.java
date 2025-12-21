package dwe.holding.admin.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import dwe.holding.admin.model.type.AgendaTypeEnum;
import dwe.holding.shared.model.type.PaymentMethodEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class LocalMemberPreferences {
    private String firstPageMessage;
    private String consultTextTemplate;
    private Integer prefEstimatedTime;
    private String openingsTimes;
    private String prefInsuranceCompany;
    private PaymentMethodEnum prefPaymentMethod;

    private String prefRoom1;
    private String prefRoom2;
    private String prefRoom3;
    private String prefRoom4;

    private AgendaTypeEnum startAgendaIn;
    private YesNoEnum active;
    private YesNoEnum prefRxLabel;
    private YesNoEnum mandatoryReason;
    private YesNoEnum sendOutAppointmentReminderMail;

    public List<Template> getConsultTextRecords(ObjectMapper objectMapper) {
        return objectMapper.readValue(getConsultTextTemplate(), TemplatesResponse.class).templates();
    }

    public record TemplatesResponse(
            @JsonProperty("Templates")
            List<Template> templates
    ) {
    }

    public record Template(
            @JsonProperty("Order")
            int order,
            @JsonProperty("Title")
            String title,
            @JsonProperty("Text")
            String text,
            @JsonProperty("Selected")
            boolean selected
    ) {
    }
}