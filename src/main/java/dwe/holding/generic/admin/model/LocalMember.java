package dwe.holding.generic.admin.model;

import dwe.holding.generic.admin.model.base.BaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "ADMIN_LOCALMEMBER", uniqueConstraints = @UniqueConstraint(name = "uk_memberLocal_name", columnNames = "NAME"))
@Entity
@SuperBuilder
@AllArgsConstructor
@Setter
@Getter
public class LocalMember extends BaseBO {
    @Column(nullable = false)
    private Long mid;
    @Column(nullable = false)
    private String localMemberName;
    private String phone1;
    private String phone2;
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String zipCode;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, updatable = false)
    private Member member;

    public LocalMember() {
    }

    // todo split out


//        @Column(nullable = false)
//    private String contactUserId;
//    @Column(length = 2000)
//    private String firstPageMessage;
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinColumn(updatable = false)
//    private Set<MemberLocalTax> memberLocalTaxs = new HashSet<MemberLocalTax>(0);
//    @Column(name = "CONSULT_TEXT_TEMPLATE", nullable = false, length = 2000)
//    private String consultTextTemplate;
//    @Column(name = "PREF_ESTIMATED_TIME", nullable = false)
//    private Integer prefEstimatedTime;
//    @Column(name = "PREF_INSURANCE_COMPANY", length = 50)
//    private String openingsTimes;
//    private String prefInsuranceCompany;
//    @Column(name = "PREF_PAYMENT_METHOD", nullable = false, length = 1)
//    private PaymentMethod prefPaymentMethod;
//    @Column(name = "PREF_ROOM1", nullable = false, length = 15)
//    private String prefRoom1;
//    @Column(name = "PREF_ROOM2", nullable = false, length = 15)
//    private String prefRoom2;
//    @Column(name = "PREF_ROOM3", nullable = false, length = 15)
//    private String prefRoom3;
//    @Column(name = "PREF_ROOM4", length = 15)
//    private String prefRoom4;
//    @Column(name = "PREF_ROOM_AGENDA", nullable = false, length = 1)
//    private AgendaType prefRoomAgenda;
//    @Column(name = "PREF_RXLABEL", nullable = false, length = 1)
//    private YesNo prefRxLabel;
//    @Column(name = "MANDATORY_REASON", nullable = false, length = 1)
//    private YesNo mandatoryReason;
//    private YesNo sendOutAppointmentReminderMail;

}