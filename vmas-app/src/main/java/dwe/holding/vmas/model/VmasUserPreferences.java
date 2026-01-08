package dwe.holding.vmas.model;

import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// Preferences are stored as JSON on User
public class VmasUserPreferences {

    @NotBlank
    private String agendaVet1;

    @NotBlank
    private String agendaVet2;

    @NotBlank
    private String agendaVet3;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum searchCustStart;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum searchCustStreet;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum searchCustNameTelephone = YesNoEnum.No;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum searchCustPet = YesNoEnum.No;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum visitAppointmentList;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum visitTotalVisit;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum visitAppointmentInfo;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum visitVisitInfo;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum visitAnalyseInfo;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum visitComments;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum visitProducts;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum visitDiagnoses;
}