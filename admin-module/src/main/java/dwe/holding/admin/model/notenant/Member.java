package dwe.holding.admin.model.notenant;

import dwe.holding.admin.model.base.BaseBO;
import dwe.holding.admin.model.tenant.LocalMember;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;


@Table(name = "ADMIN_MEMBER")
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Member extends BaseBO {

    @Column(nullable = false)
    @NotEmpty
    private String name;
    @Column(nullable = false)
    @NotEmpty
    private String password;
    @Column(nullable = false)
    @NotNull
    private Integer simultaneousUsers;
    @NotEmpty
    @Column(nullable = false)
    private String shortCode;

    // TODO fill with local member POC and set manually after migration
    private String pointOfContact;
    private String pointOfContactAddress;
    private String pointOfContactEmailAddress;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum active;

    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum localMemberSelectRequired;
    @Column(nullable = false)
    @NotNull
    private LocalDate startDate = LocalDate.now();
    private LocalDate stopDate;
    @NotEmpty
    private String applicationName;
    @NotEmpty
    private String applicationView;
    @NotEmpty
    private String applicationRedirect;

    @Transient
    List<LocalMember> localMembers;
    // todo split out to external table functionalities
//    @Column(name = "INVOICE_COUNTER", precision = 7)
//    private Long invoiceCounter;
//    @Column(name = "PAYMENT_PERIOD")
//    private Short paymentPeriod;
//    @Column(name = "REMINDER_PERIOD1")
//    private Short reminderPeriod1;
//    @Column(name = "REMINDER_PERIOD2")
//    private Short reminderPeriod2;
//    @Column(name = "USE_INVOICE_REMINDER", nullable = false, length = 1)
//    private YesNoTransition useInvoiceReminder;
//    @Column(name = "VAT_VIA_PAYMENT", nullable = false, length = 1)
//    private YesNo vatViaPayment;
//    @Column(name = "PROCESSED_DIFF", nullable = false, length = 1)
//    private YesNo processedDiff;
//    @Column(name = "USE_VENOM", nullable = false, length = 1)
//    private YesNo useVenom;
//    @Column(name = "SMTP_ADDRESS")
//    private String smtpAddress;
}