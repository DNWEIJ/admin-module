package dwe.holding.salesconsult.consult.model;

import dwe.holding.admin.model.base.LocalAndMemberBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "CONSULT_LOOKUP_ROOM")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LookupRoom extends LocalAndMemberBaseBO {
    @NotEmpty
    @Column(nullable = false)
    private String room;
}