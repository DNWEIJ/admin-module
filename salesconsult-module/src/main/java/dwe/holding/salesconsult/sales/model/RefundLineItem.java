package dwe.holding.salesconsult.sales.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Table(name = "SALES_REFUND_LINEITEM")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefundLineItem extends CostCalc {
    @ManyToOne(fetch = FetchType.LAZY)
    private Refund refund;

    public boolean getHasPrintLabel() {
        return false;
    }
}