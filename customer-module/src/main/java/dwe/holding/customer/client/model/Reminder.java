package dwe.holding.customer.client.model;


import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;

@Table(name = "CUSTOMER_REMINDER")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Reminder extends MemberBaseBO implements Serializable {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id")
    @NotNull
    private Pet pet;

    @Column(nullable = false)
    @NotNull
    private LocalDate dueDate;

    @Column(nullable = false)
    @NotBlank
    private String reminder;

    @Column(nullable = false)
    @NotNull
    private Long originatingAppointmentId;
}