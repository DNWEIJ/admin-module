package dwe.holding.customer.model.order;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity(name = "CUSTOMER_ESTIMATE")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Estimate extends TenantBaseBO {

    // TODO
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "estimate")
//    private Set<Estimatelineitem> estimatelineitems = new HashSet<>();
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "estimate")
//    private Set<Estimatespecific> estimatespecifics = new HashSet<>();

    @Column(nullable = false)
    private LocalDate estimateDate;

    private LocalDate transToVisit;
}