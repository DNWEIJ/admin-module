package dwe.holding.salesconsult.consult.model;

import dwe.holding.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Table(name = "CONSULT_ESTIMATE")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Estimate extends TenantBaseBO {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "estimate")
    private Set<Estimatelineitem> estimatelineitems = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "estimate", cascade = CascadeType.ALL)

    private Set<EstimateForPet> estimateForPets = new HashSet<>();
    private LocalDate estimateDate;
    private LocalDate transToVisit;
}