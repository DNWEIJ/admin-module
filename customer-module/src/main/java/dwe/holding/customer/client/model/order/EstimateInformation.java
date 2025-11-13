package dwe.holding.customer.client.model.order;

import dwe.holding.admin.model.base.TenantBaseBO;
import dwe.holding.customer.client.model.Pet;
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
        @JoinColumn(name = "pet_id")
        private Pet pet;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "estimate_id")
        private Estimate estimate;

        private String veterinarian;

        private String purpose;

        private String comments;
    }