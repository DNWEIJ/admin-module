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
    private Integer estimatedTime;
    private String insuranceCompany;
    private PaymentMethodEnum paymentMethod;
    private AgendaTypeEnum agendaType;

    private YesNoEnum mandatoryConsultReason;
    private YesNoEnum mandatoryExpireDate;
    private YesNoEnum sendOutAppointmentReminderMail;

    private String consultTextTemplate;

    private String room1;
    private String room2;
    private String room3;
    private String room4;

    private String openingsTimes;
    private String firstPageMessage;

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