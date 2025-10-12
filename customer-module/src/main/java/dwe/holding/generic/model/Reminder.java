package dwe.holding.generic.model;


import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;

@Entity(name = "CUSTOMER_REMINDER")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public  class Reminder extends TenantBaseBO implements Serializable {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CHILD_ID")
    private Child child;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String reminder;

    @Column(nullable = false)
    private Long originatingAppointmentId;
}