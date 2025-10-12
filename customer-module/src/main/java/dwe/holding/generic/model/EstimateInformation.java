package dwe.holding.generic.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


    @Entity(name = "CUSTOMER_ESTIMATEINFORMATION")
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public  class EstimateInformation extends TenantBaseBO {

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "CHILD_ID")
        private Child child;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "ESTIMATE_ID")
        private Estimate estimate;

        private String veterinarian;

        private String purpose;

        private String comments;
    }