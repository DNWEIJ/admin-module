package dwe.holding.salesconsult.sales.model;


import dwe.holding.customer.client.model.Pet;
import dwe.holding.salesconsult.consult.model.Appointment;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "SALES_LINE_ITEM")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LineItem extends CostCalc {

    @ManyToOne(fetch = FetchType.LAZY)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pet pet;

    private boolean hasPrintLabel;

}