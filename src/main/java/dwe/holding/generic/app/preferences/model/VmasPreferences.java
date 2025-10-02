package dwe.holding.generic.app.preferences.model;


import dwe.holding.generic.admin.model.User;
import dwe.holding.generic.admin.model.type.YesNoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VmasPreferences {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private User user;

    @Column(name = "PREF_AGENDA_VET1", length = 100)
    private String pref_agenda_vet1;
    @Column(name = "PREF_AGENDA_VET2", length = 100)
    private String pref_agenda_vet2;
    @Column(name = "PREF_AGENDA_VET3", length = 100)
    private String pref_agenda_vet3;
    @Column(name = "PREF_SEARCH_CUST_START", nullable = false, length = 1)
    private YesNoEnum prefSearchCustStart;
    @Column(name = "PREF_SEARCH_CUST_STREET", nullable = false, length = 1)
    private YesNoEnum prefSearchCustStreet;

    @Column(name = "PREF_VISIT_APPOINTMENT_LIST", nullable = false, length = 1)
    private YesNoEnum prefVisitAppointmentList;
    @Column(name = "PREF_VISIT_TOTAL_VISIT", nullable = false, length = 1)
    private YesNoEnum prefVisitTotalVisit;
    @Column(name = "PREF_VISIT_APPOINTMENT_INFO", nullable = false, length = 1)
    private YesNoEnum prefVisitAppointmentInfo;
    @Column(name = "PREF_VISIT_VISIT_INFO", nullable = false, length = 1)
    private YesNoEnum prefVisitVisitInfo;
    @Column(name = "PREF_VISIT_ANALYSE_INFO", nullable = false, length = 1)
    private YesNoEnum prefVisitAnalyseInfo;
    @Column(name = "PREF_VISIT_COMMENTS", nullable = false, length = 1)
    private YesNoEnum prefVisitComments;
    @Column(name = "PREF_VISIT_PRODUCTS", nullable = false, length = 1)
    private YesNoEnum prefVisitProducts;
    @Column(name = "PREF_VISIT_DIAGNOSES", nullable = false, length = 1)
    private YesNoEnum prefVisitDiagnoses;
    @Column(name = "PREF_VISIT_IMAGES", nullable = false, length = 1)
    private YesNoEnum prefVisitImages;
}