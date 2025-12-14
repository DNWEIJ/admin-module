package dwe.holding.admin.model.tenant;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Contain tax and taxService information of the MemberLocal.
 */
@Table(name = "ADMIN_LOCALMEMBER_TAX")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LocalMemberTax extends MemberBaseBO {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private LocalMember localMember;

    @Column(nullable = false)
    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal taxLow;
    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal taxHigh;
}