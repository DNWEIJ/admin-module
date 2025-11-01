package dwe.holding.customer.model;

import dwe.holding.generic.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class Notes extends MemberBaseBO {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(nullable = false)
    @NotNull
    private LocalDate noteDate;

    @NotBlank
    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private String staffMember;

    @Lob
    @NotNull
    private String note;
}