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
    private YesNoEnum searchCustStart = YesNoEnum.Yes;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum searchCustStreet = YesNoEnum.No;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum searchCustNameTelephone = YesNoEnum.No;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum searchCustPet = YesNoEnum.No;

    // consult screen
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum showVisitTotal = YesNoEnum.Yes;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum showCustomerPet = YesNoEnum.Yes;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum showConsult = YesNoEnum.Yes;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum showAnalyse = YesNoEnum.Yes;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum showProducts = YesNoEnum.Yes;

    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum showDiagnoses = YesNoEnum.Yes;

    public VmasUserPreferences valid() {
        // if there is a null field, we are not correct and need to reset
        if (
                agendaVet1 == null || agendaVet2 == null || agendaVet3 == null
                        || searchCustStart == null || searchCustStreet == null || searchCustNameTelephone == null || searchCustPet == null
                        || showVisitTotal == null || showCustomerPet == null || showConsult == null || showAnalyse == null || showProducts == null || showDiagnoses == null) {
            return new VmasUserPreferences(); // one or more fields are null → invalid
        }
        return this; // all fields are non-null → valid
    }
}