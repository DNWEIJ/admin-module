package dwe.holding.supplyinventory.model;

import dwe.holding.admin.model.base.LocalAndMemberBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Table(name = "SUPPLY_SUPPLYLOCAL", uniqueConstraints = @UniqueConstraint(columnNames = {"SUPPLY_ID", "LOCAL_MEMBER_ID"}))
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SupplyLocal extends LocalAndMemberBaseBO {
    private BigDecimal quantityOfPackages;
    private BigDecimal individualLeftInOpenPackage;
    private BigDecimal minQuantityForAlert;
    private BigDecimal buyQuantityPerOrder;
    @ManyToOne
    @JoinColumn(name = "supply_id")
    private Supply supply;
}