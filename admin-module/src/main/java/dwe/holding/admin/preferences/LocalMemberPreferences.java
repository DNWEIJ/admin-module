package dwe.holding.admin.preferences;

import dwe.holding.admin.model.type.AgendaTypeEnum;
import dwe.holding.shared.model.type.PaymentMethodEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.Transient;
import lombok.*;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LocalMemberPreferences {
    private Integer estimatedTime;
    private String insuranceCompany;
    private PaymentMethodEnum paymentMethod;
    private AgendaTypeEnum roomAgenda;

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

    @Transient
    private List<Template> template = new ArrayList<>();

    public List<Template> getConsultTextTemplate(ObjectMapper objectMapper) {
        return objectMapper.readValue(getConsultTextTemplate(), TemplatesResponse.class).getTemplates();
    }

    public void setConsultTextTemplate(ObjectMapper objectMapper) {
        consultTextTemplate = objectMapper.writeValueAsString(template);
    }
}
