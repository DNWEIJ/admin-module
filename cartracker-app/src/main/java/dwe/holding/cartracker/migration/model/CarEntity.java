package dwe.holding.cartracker.migration.model;


import jakarta.persistence.*;

@Entity(name = "car")
@Deprecated
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    String name;
    int kmTotal;
    @Column(name="km_per_liter",columnDefinition = "integer default 0")
    int kmPerLiter;
    @Column(name="road_tax_per_year_in_cents",columnDefinition = "integer default 0")
    int roadTaxPerYearInCents;
    @Column(name="insurance_per_year_in_cents",columnDefinition = "integer default 0")
    int insurancePerYearIncents;

    public CarEntity() {
    }

    private CarEntity(Builder builder) {
        setName(builder.name);
        setKmTotal(builder.kmTotal);
        setKmPerLiter(builder.kmPerLiter);
        setRoadTaxPerYearInCents(builder.roadTaxPerYear);
        setInsurancePerYearIncents(builder.insurancePerYear);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String carType) {
        this.name = carType;
    }

    public int getKmTotal() {
        return kmTotal;
    }

    public void setKmTotal(int kmTotal) {
        this.kmTotal = kmTotal;
    }

    public int getKmPerLiter() {
        return kmPerLiter;
    }

    public void setKmPerLiter(int kmPerLiter) {
        this.kmPerLiter = kmPerLiter;
    }

    public int getRoadTaxPerYearInCents() {
        return roadTaxPerYearInCents;
    }

    public void setRoadTaxPerYearInCents(int roadTaxPerYear) {
        this.roadTaxPerYearInCents = roadTaxPerYear;
    }

    public int getInsurancePerYearIncents() {
        return insurancePerYearIncents;
    }

    public void setInsurancePerYearIncents(int insurancePerYear) {
        this.insurancePerYearIncents = insurancePerYear;
    }


/** BUILDER **/

    public static final class Builder {
        private String name;
        private int kmTotal;
        private int kmPerLiter;
        private int roadTaxPerYear;
        private int insurancePerYear;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder kmTotal(int val) {
            kmTotal = val;
            return this;
        }

        public Builder kmPerLiter(int val) {
            kmPerLiter = val;
            return this;
        }

        public Builder roadTaxPerYear(int val) {
            roadTaxPerYear = val;
            return this;
        }

        public Builder insurancePerYear(int val) {
            insurancePerYear = val;
            return this;
        }

        public CarEntity build() {
            return new CarEntity(this);
        }
    }
}