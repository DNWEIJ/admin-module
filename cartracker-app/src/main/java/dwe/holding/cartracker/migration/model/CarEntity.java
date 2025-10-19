package dwe.holding.cartracker.migration.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Deprecated
public class CarEntity extends TenantBaseBO {
     String name;
    int kmTotal;
    @Column(name="km_per_liter",columnDefinition = "integer default 0")
    int kmPerLiter;
    @Column(name="road_tax_per_year_in_cents",columnDefinition = "integer default 0")
    int roadTaxPerYearInCents;
    @Column(name="insurance_per_year_in_cents",columnDefinition = "integer default 0")
    int insurancePerYearIncents;
}