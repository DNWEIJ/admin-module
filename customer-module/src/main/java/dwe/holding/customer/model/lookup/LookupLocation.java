package dwe.holding.customer.model.lookup;

import dwe.holding.generic.admin.model.base.BaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "CUSTOMER_LOOKUPLOCATION")
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public  class LookupLocation extends BaseBO   {
        @Column(nullable = false)
        private String nomenclature;
    }