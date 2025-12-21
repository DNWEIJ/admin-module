package dwe.holding.salesconsult.consult.model;

import dwe.holding.admin.model.base.MemberBaseBO;
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
public class AnalyseItem extends MemberBaseBO {
    private Long appointmentId;
    private Long patientId;
    private Long costingId;
    private Long analyseId;
    private YesNoEnum vetindicator;
    private YesNoEnum ownerindicator;
    private String comment;
    private String nomenclature;
    private Double quantity;
}