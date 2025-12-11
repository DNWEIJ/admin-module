package dwe.holding.vmas.model;

import dwe.holding.admin.model.type.AgendaTypeEnum;
import dwe.holding.shared.model.type.PaymentMethodEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
}