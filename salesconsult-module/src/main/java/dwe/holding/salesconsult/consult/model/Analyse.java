package dwe.holding.salesconsult.consult.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.supplyinventory.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Table(name = "CONSULT_ANALYSE")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Analyse extends MemberBaseBO {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "analyse_description_id")
    private AnalyseDescription analyseDescription;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Long lookupProductCategoryId;
    @NotNull
    @Column(nullable = false)
    private BigDecimal quantity;
}