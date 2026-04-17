package dwe.holding.salesconsult.consult.model;

import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Table(name = "SALES_PRESCRIPTION_LABEL")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PrescriptionLabel extends MemberBaseBO {
    @Column(nullable = false)
    private Long appointmentId;
    @Column(nullable = false)
    private Long lineItemId;
    @Column(nullable = false)
    private String ownerName;
    @Column(nullable = false)
    private String petName;
    @Column(nullable = false)
    private String staffMember;
    @Column(nullable = false)
    private LocalDate expirationDate;
    @Column(nullable = false)
    private String drugDosage;
    @Lob
    @Column(nullable = false)
    private String usageDescription;
}
