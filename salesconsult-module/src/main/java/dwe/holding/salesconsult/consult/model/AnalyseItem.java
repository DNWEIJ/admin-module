package dwe.holding.salesconsult.consult.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.shared.model.type.YesNoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Column(nullable = false)
    private Long appointmentId;
    @NotNull
    @Column(nullable = false)
    private Long petId;
    @NotNull
    @Column(nullable = false)
    private Long analyseId;
    @NotNull
    @Column(nullable = false)
    private long costingId;
    @NotNull
    @Column(nullable = false)
    private String nomenclature;
    @NotNull
    @Column(nullable = false)
    private YesNoEnum vetIndicator;
    @NotNull
    @Column(nullable = false)
    private YesNoEnum ownerIndicator;
    @NotNull
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal quantity;

    private String comment;
}