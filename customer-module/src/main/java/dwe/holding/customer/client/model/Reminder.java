package dwe.holding.customer.client.model;


import dwe.holding.admin.model.base.MemberBaseBO;
import dwe.holding.shared.model.converter.YesNoEnumConverter;
import dwe.holding.shared.model.type.YesNoEnum;
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
public class Reminder extends MemberBaseBO {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id")
    @NotNull
    private Pet pet;

    @Column(nullable = false)
    @NotNull
    private LocalDate dueDate;

    @Column(nullable = false)
    @NotBlank
    private String reminderText;

    private Long originatingAppointmentId;
    @Column(columnDefinition = "varchar(1)", nullable = false)
    @Convert(converter = YesNoEnumConverter.class)
    private YesNoEnum hasBeenNotified;
}