package dwe.holding.customer.client.model.order;

import dwe.holding.admin.model.base.TenantBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Table(name = "CUSTOMER_ESTIMATE")
@Entity
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