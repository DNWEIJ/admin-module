package dwe.holding.generic.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity(name = "CUSTOMER_NOTES")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notes extends TenantBaseBO {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CHILD_ID", nullable = false)
    private Child child;


    @Column(nullable = false)
    private LocalDate noteDate;


    @Column(nullable = false)
    private String purpose;


    @Column(nullable = false)
    private String staffMember;

    private String notes;
}