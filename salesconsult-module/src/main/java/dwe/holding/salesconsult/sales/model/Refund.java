package dwe.holding.salesconsult.sales.model;

import dwe.holding.admin.model.base.LocalAndMemberBaseBO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Table(name = "SALES_REFUND")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Refund extends LocalAndMemberBaseBO {
    @Column(nullable = false)
    private Long customerId;
    @Column(nullable = false)
    private LocalDate refundDate;
    @Column(nullable = false)
    private BigDecimal amount;
    @Lob
    private String comments;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "refund")
    @Builder.Default
    private List<RefundLineItem> refundLineItems = new ArrayList<>(0);
}