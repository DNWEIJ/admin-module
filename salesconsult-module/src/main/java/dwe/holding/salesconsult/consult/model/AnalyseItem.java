package dwe.holding.salesconsult.consult.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.shared.model.type.TaxedTypeEnum;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Table(name = "CONSULT_ANALYSE_ITEM")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AnalyseItem extends MemberBaseBO {
    private Long appointmentId;
    private Long petId;
    private Long costingId;
    private Long analyseId;
    private YesNoEnum vetIndicator;
    private YesNoEnum ownerIndicator;
    private String nomenclature;
    private BigDecimal quantity;
    private BigDecimal exclPrice;
    private BigDecimal inclPrice;
    private TaxedTypeEnum taxedType;
    private String comment;
}