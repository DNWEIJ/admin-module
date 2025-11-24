package dwe.holding.admin.model;

import dwe.holding.admin.model.base.BaseBO;
import dwe.holding.admin.model.type.AgendaTypeEnum;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.PaymentMethodEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Table(name = "ADMIN_LOCALMEMBER_PREFERENCES", uniqueConstraints = @UniqueConstraint(name = "uk_memberLocal_name", columnNames = "local_member_id"))
@Entity
@SuperBuilder
@AllArgsConstructor
@Setter
@Getter
public class LocalMemberPreferences extends BaseBO {
    @Lob
    private String firstPageMessage;
    @Lob
    private String consultTextTemplate;
    @Column(nullable = false)
    private Integer prefEstimatedTime;
    @Column(nullable = false)
    private String openingsTimes;
    private String prefInsuranceCompany;
    @Column(nullable = false)
    private PaymentMethodEnum prefPaymentMethod;

    private String prefRoom1;
    private String prefRoom2;
    private String prefRoom3;
    private String prefRoom4;

    @Column(nullable = false)
    private AgendaTypeEnum startAgendaIn;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum active;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum prefRxLabel;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum mandatoryReason;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum sendOutAppointmentReminderMail;

    public LocalMemberPreferences() {
    }


}