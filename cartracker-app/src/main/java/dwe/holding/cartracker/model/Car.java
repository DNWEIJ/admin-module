package dwe.holding.cartracker.model;


import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "CAR_CAR")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Car extends TenantBaseBO {
     String name;
    int kmTotal;
    @Column(name="km_per_liter",columnDefinition = "integer default 0")
    int kmPerLiter;
    @Column(name="road_tax_per_year_in_cents",columnDefinition = "integer default 0")
    int roadTaxPerYearInCents;
    @Column(name="insurance_per_year_in_cents",columnDefinition = "integer default 0")
    int insurancePerYearIncents;
}