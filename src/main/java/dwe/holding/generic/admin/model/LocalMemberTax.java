package dwe.holding.generic.admin.model;

import dwe.holding.generic.admin.model.base.BaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Contain tax and taxService information of the MemberLocal.
 */
@Table(name = "ADMIN_LOCALMEMBER_TAX")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

public class LocalMemberTax extends BaseBO {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private LocalMember localMember;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Instant startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE", length = 23)
    private Instant endDate;
//    @Column(name="TAX_LOW", TYPE="BIGDECIMAL(10,2)", precision=18)
//     private Double taxLow;
//    @Column(name="TAX_HIGH", precision=18)
//     private Double taxHigh;


}