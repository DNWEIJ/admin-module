package dwe.holding.salesconsult.consult.model;

import dwe.holding.salesconsult.sales.model.CostCalc;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "CONSULT_ANALYSE_ITEM")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AnalyseItem extends CostCalc {
    private Long appointmentId;
    private Long petId;
    private Long analyseId;
    private YesNoEnum vetIndicator;
    private YesNoEnum ownerIndicator;
    private String comment;
}