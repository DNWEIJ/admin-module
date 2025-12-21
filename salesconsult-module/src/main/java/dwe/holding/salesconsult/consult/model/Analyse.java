package dwe.holding.salesconsult.consult.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.supplyinventory.model.Costing;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "CONSULT_ANALYSE")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Analyse extends MemberBaseBO {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "analyse_description_id", insertable = false, updatable = false, nullable = false)
    private AnalyseDescription analyseDescription;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "costing_id", insertable = false, updatable = false, nullable = false)
    private Costing costing;

    @Column(nullable = false)
    private Long lookupCostingCategoryId;
    @NotNull
    @Column(nullable = false)
    private Double quantity;
}